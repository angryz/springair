springair 是基于 Spring Framework 的一些扩展, 便于支持一些特定的开发需求.

### 构建

工程采用 gradle 进行构建, 对所有模块同时进行构建可在工程根目录执行:

```
gradle build
```

也可进入到具体模块的路径下执行上面命令对单个模块进行构建, 或者在工程根目录执行:

```
gradle :modulename:build
```

只要将其中的 _modulename_ 替换为想要构建的模块名称即可.

Gradle 的使用请参考 [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)

