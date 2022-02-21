## 国家行政区划代码HTML解析

###（1.1）规则：

1. 省级（含省、自治区、直辖市、特别行政区）：第1、2位
2. 地级（含省辖行政区、地级市、[自治州](https://baike.baidu.com/item/%E8%87%AA%E6%B2%BB%E5%B7%9E/1710336)
   、地区、盟及中央直辖市所属市辖区和县的汇总码和省或自治区直辖县级行政区划汇总码）：第3、4位
3. 县级（市辖区、县级市、旗）：第5、6位

> 特殊处理：直辖市、非地级市
针对直辖市，需剔除中间3-4位的01的层级，直接挂载到省级；
针对海南省非地级市，需剔除中间3-4位的90的层级，直接挂载到省级；
>

###（1.2）参考资料：

百度百科关于行政区划代码的GB2260的解释：[https://www.notion.so/40de824f3a8745bb946ee0dd266941bf#2415568dc1a6406cba453243526e3b93](https://www.notion.so/40de824f3a8745bb946ee0dd266941bf)

国家民政部官网2020年行政区划代码：[http://www.mca.gov.cn/article/sj/xzqh/2020/20201201.html](http://www.mca.gov.cn/article/sj/xzqh/2020/20201201.html)

###（1.3）HTML解析实现规则：

1. 解析出每行编码与名称，并作List<Obj>存储
2. 按照区划代码1-6位，按照约定的数据结构分拆，数据结构示例：

```java
{
    "21": {
        "element": {
            "code": "210000",
            "name": "省"
        },
        "childNodes": {
            "10": {
                "element": {
                    "code": "211000",
                    "name": "市"
                },
                "childNodes": {
                    "01": {
                        "element": {
                            "code": "211001",
                            "name": "区1"
                        },
                        "childNodes": {}
                    },
                    "02": {
                        "element": {
                            "code": "211002",
                            "name": "区2"
                        },
                        "childNodes": {}
                    }
                }
            }
        }
    }
}
```

1. 针对直辖市、非地级市进行特殊处理
2. 生成Insert语句，语句示例：

```sql
INSERT INTO TBL(code, name, parent_code) VALUES (xxx, xxx, xxx);
```