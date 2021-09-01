# mongo

## 过期索引
> TTL索引：`Time To Live`的缩写。在指定时间段删除数据。

### 1.固定过期时间
> 示例`lastModifiedDate`日期类型，作为过期索引字段，`expireAfterSeconds`为过期索引属性，单位为秒。
```
db.eventlog.createIndex(
    { "lastModifiedDate": 1 },
    { expireAfterSeconds: 3600 }
)
```
### 2.调整过期时间
> 使用`collMod`调整过期时间。
```
db.runCommand({
    collMod: "eventlog", ---集合名
    index: { 
        keyPattern: { lastModifiedDate: 1 }, ---createTime为具有TTL索引的字段名
        expireAfterSeconds: 7200  ---修改后的过期时间(秒)
    }
})
```
### 3.动态过期时间
> 建立索引时设置`expireAfterSeconds`属性为`0`
```
db.eventlog.createIndex( { "lastModifiedDate": 1 }, { expireAfterSeconds: 0 } )
```
**插入测试数据：指定3天后过期:**
```
db.eventlog.insert( {
   "lastModifiedDate": new Date(ISODate().getTime() + 1000 * 3600 * 24 * 3)
} )
```
### 4.TTL索引注意项
> - 过期索引不保证过期的数据立马会删除,MongoDB后台线程会每隔60秒扫描一次是否有过期数据
> - 在副本集的模式中，后台线程只会删除`primary`节点的数据。`secondary`节点会从primary节点复制删除操作。
> - 过期索引只能使用在单一字段上，不能用在复合索引中。
> - `_id`字段不能创建过期索引。
> - 过期索引不能用在`Capped Collections`固定大小的集合中。
> - 不能使用`createIndex()`来调整过期时间，可以使用`collMod`命令来修改。
> - 如果某个字段（单字段）已经是非过期索引，则无法在同一字段上创建过期索引。必须首先删除原先的索引，然后使用`expireAfterSeconds`选项重新创建。

PS:**与`Redis`的过期自动删除数据相比，`MongoDB`的自动删除数据不能保证原子性**


### 5.示例

```
db.getCollection("dn-index-sample").find({}).limit(10)

// 创建ttl索引定点过期： expireAt
db.getCollection("dn-index-sample").createIndex({ "expireAt": 1 }, { expireAfterSeconds: 0 } )
// 创建ttl索引固定时间过期： createAt后300s过期
db.getCollection("dn-index-sample").createIndex({ "createAt": 1 }, { expireAfterSeconds: 300 } )
// 更改ttl索引的时间： createAt后600s过期
db.runCommand({ collMod:"dn-index-sample",index:{keyPattern:{createAt:1},expireAfterSeconds: 600 }})
// 查询索引
db.getCollection("dn-index-sample").getIndexes()

// 测试新增数据：
// #1 createAt 60s后过期，expireAt 180s后过期；预期结果：createAt生效，60s后数据删除
db.getCollection("dn-index-sample").insert({
   "createAt": new Date(ISODate().getTime()-1000*540),
   "expireAt": new Date(ISODate().getTime()+1000*180),
   "name": "dean-001"
   }
)
// #2 createAt 600s后过期，expireAt 300s后过期；预期结果：expireAt生效，300s后数据删除
db.getCollection("dn-index-sample").insert({
   "createAt": new Date(ISODate().getTime()),
   "expireAt": new Date(ISODate().getTime()+1000*300),
   "name": "dean-001"
   }
)
```

## 服务状态
```
// 查看MongoDB的连接信息
db.serverStatus().connections
// 使用db.currentOP()方法查看进程信息
db.currentOP()
// 查看db1数据库执行时间超过3秒的活动进程
db.currentOP(
  {
      "active" : true,
      "secs_running":{"$gt":3},
      "ns":/^db1\./
  }
)
// MongoDB杀死正在执行的进程
db.killOp(opid);
```


## 常用操作

```shell
// 使用数据库：dn-test
use dn-test
// 创建集合：user
db.createCollection("user")
// 创建索引：createTime，降序，索引名：exp_time,过期时间
db.user.createIndex( { "createTime": 1 },{ "name": "exp_time",expireAfterSeconds:3600*24})
// PS: 建立索引时，默认时 “foreground” 也就是前台建立索引，但是，当你的数据库数据量很大时，在建立索引的时会读取数据文件，大量的文件读写会阻止其他的操作，所以命令会锁库,可以显示指定background
// 这样就不会锁库了，建立索引就会在后台处理了。（注：“{key:1}” 中，1 表示升序 - asc，-1 表示降序 - desc ）
// 在后台建立索引的时候，不能对建立索引的 collection 进行一些坏灭型的操作，如：运行 repairDatabase，drop，compat，当你在建立索引的时候运行这些操作的会报错
db.user.createIndex({key:1},{background: true})
// 添加数据
db.user.insert({"name": "dean01", "age": 29,"createTime": new Date})
db.user.insert({"name": "dean02", "age": 22,"createTime": new Date})
db.user.insert({"name": "dean03", "age": 10,"createTime": new Date})
db.user.insert({"name": "dean04", "age": 15,"createTime": new Date})
// 查询 name=dean01的数据
db.user.find({"name": "dean01"})
db.user.find().limit(10)
// Dinstinct按指定字段统计,等价于：select count(Distinct age) from user;
db.getCollection("user").distinct("age").length

// 查询 age > 10的数据（条件查询）
db.getCollection("user").find(
    { 
        "age" : { 
            "$gt" : NumberLong(10)
        }
    }
);
// 查询 age > 10 and age< 20的数据（多条件查询）
db.getCollection("user").find(
    { 
        "$and" : [
            { 
                "age" : { 
                    "$gt" : NumberLong(10)
                }
            }, 
            { 
                "age" : { 
                    "$lt" : NumberLong(20)
                }
            }
        ]
    }
);
// 获取索引列表
db.user.getIndexes()
```

## refer to 

- https://docs.mongodb.com/manual/tutorial/expire-data/
