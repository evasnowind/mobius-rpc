
#

## 编写Consumer

consumer获取初始化对象的方式
1、根据spring上下文都初始化完之后，扫描bean
2、显式创建一个consumerStrap，加一个 方法，在applicationRunner
3、spring instantiationAware 处理bean中的属性

consumer端一般叫stub
provider端的对象一般叫skeleton