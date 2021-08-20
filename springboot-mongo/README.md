

### springboot-mongo

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
