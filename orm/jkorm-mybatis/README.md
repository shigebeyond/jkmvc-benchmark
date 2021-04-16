## 执行性能检查程序
```
jcd jkmvc-benchmark/orm/jkorm-mybatis
gradle build -x test -Pall
cd build/jkorm-mybatis
rm logs/*
./start-jkorm.sh
./start-mybatis.sh
```

## 对比结果查看
```
jcd jkbenchmark/jkbenchmark-web
gradle build -x test -Pall
cd build/libs
./start-jetty.sh
```