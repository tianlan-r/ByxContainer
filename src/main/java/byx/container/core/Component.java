package byx.container.core;

import byx.container.util.ReflectUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 组件：能够从IOC容器中获取的一个对象。
 * 所有的组件都被IOC容器管理，每个组件都封装了自己与其他组件的依赖关系。
 * 换句话说，每个组件都知道如何创建自己，这些信息封装在create方法中。
 * 每次从IOC容器中获取某个组件时，该组件的create方法将被调用。
 * Component中还封装了一些常用的静态方法和默认方法
 */
public interface Component
{
    /**
     * 创建组件
     * @return 组件对象
     */
    Object create();

    /**
     * 创建ValueComponent
     * @param value 值
     * @return 用value构造的ValueComponent
     */
    static Component value(Object value)
    {
        return new ValueComponent(value);
    }

    /**
     * 创建构造函数Component
     * @param type 类型
     * @param params 参数组件
     * @return 构造函数Component
     */
    static Component constructor(Class<?> type, Component... params)
    {
        return new FunctionComponent(Function.constructor(type), params);
    }

    /**
     * 创建静态工厂Component
     * @param type 类型
     * @param name 方法名
     * @param params 参数组件
     * @return 静态工厂Component
     */
    static Component staticFactory(Class<?> type, String name, Component... params)
    {
        return new FunctionComponent(Function.staticFactory(type, name), params);
    }

    /**
     * 创建实例工厂Component
     * @param instance 实例组件
     * @param name 方法名
     * @param params 参数组件
     * @return 实例工厂Component
     */
    static Component instanceFactory(Component instance, String name, Component... params)
    {
        return instance.map(obj -> new FunctionComponent(Function.instanceFactory(obj, name), params).create());
    }

    /**
     * 对当前Component创建的结果进行转换
     * @param mapper 转换器
     * @return 用MapperComponent包装后的Component
     */
    default Component map(Mapper mapper)
    {
        return new MapperComponent(this, mapper);
    }

    /**
     * 设置当前Component创建的对象的属性
     * @param name 属性名
     * @param value 属性值
     * @return 用MapperComponent包装后的Component
     */
    default Component setProperty(String name, Component value)
    {
        return this.map(obj ->
        {
            ReflectUtils.setProperty(obj, name, value.create());
            return obj;
        });
    }

    /**
     * 在当前Component创建后的对象上调用setter方法
     * @param name setter方法名称
     * @param params setter方法参数
     * @return 用MapperComponent包装后的Component
     */
    default Component invokeSetter(String name, Component... params)
    {
        return this.map(obj ->
        {
            ReflectUtils.call(obj, name, Arrays.stream(params).map(Component::create).toArray());
            return obj;
        });
    }

    /**
     * 让当前组件变成单例
     * @return 用SingletonComponent包装的当前组件
     */
    default Component singleton()
    {
        return new SingletonComponent(this);
    }

    /**
     * 创建容器引用组件
     * @param container 容器
     * @param id 组件的唯一标识
     * @return 对容器中指定id的组件的引用
     */
    static Component reference(Container container, String id)
    {
        return new ReferenceComponent(container, id);
    }

    /**
     * 将多个组件的创建结果封装成列表
     * @param components 多个组件
     * @return ListComponent包装类
     */
    static Component list(Component... components)
    {
        return new ListComponent(components);
    }

    /**
     * 将多个组件的创建结果封装成集合
     * @param components 多个组件
     * @return SetComponent包装类
     */
    static Component set(Component... components)
    {
        return new SetComponent(components);
    }

    /**
     * 将多个组件的创建结果封装成map
     * @param componentMap 组件map
     * @return MapComponent包装类
     */
    static Component map(Map<Component, Component> componentMap)
    {
        return new MapComponent(componentMap);
    }
}
