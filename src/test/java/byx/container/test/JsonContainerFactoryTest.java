package byx.container.test;

import byx.container.Container;
import byx.container.component.Component;
import byx.container.component.PostProcessor;
import byx.container.exception.*;
import byx.container.factory.ContainerFactory;
import byx.container.factory.json.JsonContainerFactory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonContainerFactoryTest
{
    public static class Student
    {
        private int id;
        private String name;
        private List<Integer> scores;

        public Student()
        {
            id = -1;
            name = "unknown_name";
            scores = new ArrayList<>();
        }

        public Student(int id, String name, List<Integer> scores)
        {
            this.id = id;
            this.name = name;
            this.scores = scores;
        }

        public static Student createDefault()
        {
            return new Student();
        }

        public static Student create(int id, String name, List<Integer> scores)
        {
            return new Student(id, name, scores);
        }

        public void setIdAndName(int id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String username)
        {
            this.name = username;
        }

        public List<Integer> getScores()
        {
            return scores;
        }

        public void setScores(List<Integer> scores)
        {
            this.scores = scores;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            Student student = (Student) o;
            return id == student.id && Objects.equals(name, student.name) && Objects.equals(scores, student.scores);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(id, name, scores);
        }
    }

    public static class Student2 extends Student
    {
        private int age;
        boolean male;

        public int getAge()
        {
            return age;
        }

        public void setAge(int age)
        {
            this.age = age;
        }

        public boolean isMale()
        {
            return male;
        }

        public void setMale(boolean male)
        {
            this.male = male;
        }
    }

    public static class StringComponent implements Component
    {
        private final String s;
        private final int i;

        public StringComponent()
        {
            this.s = "hello";
            this.i = 123;
        }

        public StringComponent(String s, int i)
        {
            this.s = s;
            this.i = i;
        }

        @Override
        public Object create()
        {
            return s + ": " + i;
        }

        @Override
        public Class<?> getType()
        {
            return String.class;
        }
    }

    public static class AppendProcessor implements PostProcessor
    {
        private final String s;

        public AppendProcessor()
        {
            this("hello");
        }

        public AppendProcessor(String s)
        {
            this.s = s;
        }

        @Override
        public void process(Object obj)
        {
            ((StringBuilder) obj).append(s);
        }
    }

    public interface UserDao {}
    public static class UserDaoImpl implements UserDao {}
    public interface UserService {}
    public static class UserServiceImpl implements UserService
    {
        private final UserDao userDao;

        public UserServiceImpl(UserDao userDao)
        {
            this.userDao = userDao;
        }

        public UserDao getUserDao()
        {
            return userDao;
        }
    }

    /**
     * 常数
     */
    @Test
    public void test1()
    {
        assertThrows(ByxContainerException.class,
                () -> new JsonContainerFactory(null));

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test1.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        int c1 = container.getObject("intValue");
        assertEquals(123, c1);
        double c2 = container.getObject("doubleValue");
        assertEquals(3.14, c2);
        String c3 = container.getObject("stringValue");
        assertEquals("hello", c3);
        boolean c4 = container.getObject("trueValue");
        assertTrue(c4);
        boolean c5 = container.getObject("falseValue");
        assertFalse(c5);
        Object c6 = container.getObject("nullValue");
        assertNull(c6);
    }

    /**
     * 集合类型
     */
    @Test
    public void test2()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test2.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        List<?> c1 = container.getObject("c1");
        assertTrue(c1.isEmpty());
        List<Integer> c2 = container.getObject("c2");
        assertEquals(List.of(100), c2);
        List<Integer> c3 = container.getObject("c3");
        assertEquals(List.of(123, 456, 789), c3);
        List<Object> c4 = container.getObject("c4");
        assertEquals(List.of(123, "hello", 456), c4);
        Set<?> c5 = container.getObject("c5");
        assertEquals(Collections.EMPTY_SET, c5);
        Set<Integer> c6 = container.getObject("c6");
        assertEquals(Set.of(888), c6);
        Set<String> c7 = container.getObject("c7");
        assertEquals(Set.of("aaa", "bbb", "ccc"), c7);
        Set<Object> c8 = container.getObject("c8");
        assertEquals(Set.of(123, "bbb", "ccc"), c8);
        Map<?, ?> c9 = container.getObject("c9");
        assertEquals(Collections.EMPTY_MAP, c9);
        Map<String, Integer> c10 = container.getObject("c10");
        assertEquals(Map.of("k1", 123, "k2", 456, "k3", 789), c10);
        Map<String, Object> c11 = container.getObject("c11");
        assertEquals(Map.of("k", "v"), c11);
        Map<String, Object> c12 = container.getObject("c12");
        assertEquals(Map.of("key1", 111, "key2", "hello", "key3", 222), c12);
        Map<?, ?> c13 = container.getObject("c13");
        assertEquals(Collections.EMPTY_MAP, c13);
        Map<Integer, String> c14 = container.getObject("c14");
        assertEquals(Map.of(123, "aaa"), c14);
        Map<Integer, String> c15 = container.getObject("c15");
        assertEquals(Map.of(123, "aaa", 456, "bbb", 789, "ccc"), c15);
        Map<Integer, Object> c16 = container.getObject("c16");
        assertEquals(Map.of(123, "aaa", 456, 999, 789, "ccc"), c16);
        List<Student> c17 = container.getObject("c17");
        assertEquals(3, c17.size());
        assertEquals(new Student(1001, "byx", List.of(10, 20, 30)), c17.get(0));
        assertEquals(new Student(1002, "XiaoMing", List.of(40, 50, 60)), c17.get(1));
        assertEquals(new Student(1003, "XiaoHua", List.of(70, 80, 90)), c17.get(2));
    }

    /**
     * 引用
     */
    @Test
    public void test3()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test3.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        String c1 = container.getObject("c1");
        assertEquals("hello", c1);
        int c2 = container.getObject("c2");
        assertEquals(123, c2);
        String c3 = container.getObject("c3");
        assertEquals("hello", c3);
        int c4 = container.getObject("c4");
        assertEquals(123, c4);
        String c5 = container.getObject("c5");
        assertEquals("hello", c5);
        List<String> c6 = container.getObject("c6");
        assertEquals(List.of("hello", "powerful", "byx", "container", "!"), c6);
        String c7 = container.getObject("c7");
        assertEquals("hi", c7);
        String c8 = container.getObject("c8");
        assertEquals("thank you", c8);
    }

    /**
     * 构造函数
     */
    @Test
    public void test4()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test4.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        String c1 = container.getObject("c1");
        assertEquals("", c1);
        Student c2 = container.getObject("c2");
        assertEquals(-1, c2.getId());
        assertEquals("unknown_name", c2.getName());
        assertEquals(Collections.EMPTY_LIST, c2.getScores());
        String c3 = container.getObject("c3");
        assertEquals("hello", c3);
        Student c4 = container.getObject("c4");
        assertEquals(1001, c4.getId());
        assertEquals("byx", c4.getName());
        assertEquals(List.of(88.5, 97.5, 90), c4.getScores());
        Student c5 = container.getObject("c5");
        assertEquals(1002, c5.getId());
        assertEquals("XiaoMing", c5.getName());
        assertEquals(List.of(69.5, 87, 77), c5.getScores());
    }

    /**
     * 静态工厂
     */
    @Test
    public void test5()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test5.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        List<?> c1 = container.getObject("c1");
        assertEquals(Collections.emptyList(), c1);
        String c2 = container.getObject("c2");
        assertEquals("123", c2);
        Student c3 = container.getObject("c3");
        assertEquals(-1, c3.getId());
        assertEquals("unknown_name", c3.getName());
        assertEquals(Collections.EMPTY_LIST, c3.getScores());
        Student c4 = container.getObject("c4");
        assertEquals(1001, c4.getId());
        assertEquals("byx", c4.getName());
        assertEquals(List.of(88.5, 97.5, 90), c4.getScores());
        Student c5 = container.getObject("c5");
        assertEquals(1002, c5.getId());
        assertEquals("XiaoMing", c5.getName());
        assertEquals(List.of(69.5, 87, 77), c5.getScores());
    }

    /**
     * 实例工厂
     */
    @Test
    public void test6()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test6.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        int c1 = container.getObject("c1");
        assertEquals(5, c1);
        String c2 = container.getObject("c2");
        assertEquals("ppl", c2);
        String c3 = container.getObject("c3");
        assertEquals("unknown_name", c3);
        String c4 = container.getObject("c4");
        assertEquals("ell", c4);
        String c5 = container.getObject("c5");
        assertEquals("he", c5);
    }

    /**
     * 属性注入
     */
    @Test
    public void test7()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test7.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        Student c1 = container.getObject("c1");
        assertEquals(1001, c1.getId());
        assertEquals("byx", c1.getName());
        assertEquals(List.of(70, 80, 90), c1.getScores());
        Student2 c2 = container.getObject("c2");
        assertEquals(1002, c2.getId());
        assertEquals("XiaoMing", c2.getName());
        assertEquals(21, c2.getAge());
        assertTrue(c2.isMale());
        assertEquals(List.of(70, 60, 50), c2.getScores());
        Student c3 = container.getObject("c3");
        assertEquals(1003, c3.getId());
        assertEquals("XiaoHong", c3.getName());
        assertEquals(List.of(10, 20, 30), c3.getScores());
        Student c4 = container.getObject("c4");
        assertEquals(1004, c4.getId());
        assertEquals("XiaoHua", c4.getName());
        assertEquals(List.of(10, 20, 30), c4.getScores());
    }

    /**
     * setter注入
     */
    @Test
    public void test8()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test8.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        Student c1 = container.getObject("c1");
        assertEquals(1001, c1.getId());
        assertEquals("byx", c1.getName());
        assertEquals(List.of(90, 85, 80), c1.getScores());
        Student c2 = container.getObject("c2");
        assertEquals(1002, c2.getId());
        assertEquals("XiaoMing", c2.getName());
        assertEquals(List.of(75, 80, 85), c2.getScores());
        Student c3 = container.getObject("c3");
        assertEquals(1003, c3.getId());
        assertEquals("XiaoHua", c3.getName());
        assertEquals(List.of(50, 60, 70), c3.getScores());
        Student c4 = container.getObject("c4");
        assertEquals(1004, c4.getId());
        assertEquals("XiaoJun", c4.getName());
        assertEquals(List.of(90, 95, 100), c4.getScores());
    }

    /**
     * 条件注入
     */
    @Test
    public void test9()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test9.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        String c1 = container.getObject("c1");
        assertEquals("hello", c1);
        String c2 = container.getObject("c2");
        assertEquals("hi", c2);
        int c3 = container.getObject("c3");
        assertEquals(123, c3);
        String c4 = container.getObject("c4");
        assertEquals("ell", c4);
        int c5 = container.getObject("c5");
        assertEquals(888, c5);
        String c6 = container.getObject("c6");
        assertEquals("el", c6);
    }

    /**
     * 单例
     */
    @Test
    public void test10()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test10.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        Student c11 = container.getObject("c1");
        Student c12 = container.getObject("c1");
        assertSame(c11, c12);
        Student c21 = container.getObject("c2");
        Student c22 = container.getObject("c2");
        assertSame(c21, c22);
        Student c31 = container.getObject("c3");
        Student c32 = container.getObject("c3");
        assertNotSame(c31, c32);
        List<Integer> c41 = container.getObject("c4");
        List<Integer> c42 = container.getObject("c4");
        assertSame(c41, c42);
        List<Integer> c51 = container.getObject("c5");
        List<Integer> c52 = container.getObject("c5");
        assertSame(c51, c52);
        List<Integer> c61 = container.getObject("c6");
        List<Integer> c62 = container.getObject("c6");
        assertNotSame(c61, c62);
    }

    /**
     * 后置处理器
     */
    @Test
    public void test11()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test11.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        StringBuilder c1 = container.getObject("c1");
        assertEquals("byx hello", c1.toString());
        StringBuilder c2 = container.getObject("c2");
        assertEquals("XiaoMing hi", c2.toString());
        StringBuilder c3 = container.getObject("c3");
        assertEquals("XiaoHong goodbye", c3.toString());
        StringBuilder c4 = container.getObject("c4");
        assertEquals("XiaoHua bye", c4.toString());
    }

    /**
     * 自定义组件
     */
    @Test
    public void test12()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test12.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        String c1 = container.getObject("c1");
        assertEquals("hello: 123", c1);
        String c2 = container.getObject("c2");
        assertEquals("hi: 888", c2);
        String c3 = container.getObject("c3");
        assertEquals("ok: 999", c3);
        String c4 = container.getObject("c4");
        assertEquals("aaa: 111", c4);
        String c5 = container.getObject("c5");
        assertEquals("bbb: 222", c5);
    }

    /**
     * 类型别名
     */
    @Test
    public void test13()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test13.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        int c1 = container.getObject("c1");
        assertEquals(123, c1);
        String c2 = container.getObject("c2");
        assertEquals("hello", c2);
        Student c3 = container.getObject("c3");
        assertEquals(1001, c3.getId());
        assertEquals("byx", c3.getName());
        assertEquals(List.of(90, 70, 80), c3.getScores());
    }

    /**
     * 类型匹配
     */
    @Test
    public void test14()
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("general/test14.json");
        ContainerFactory factory = new JsonContainerFactory(inputStream);
        Container container = factory.create();

        String c1 = container.getObject("c1");
        assertEquals("hello", c1);
        assertEquals("hello", container.getObject(String.class));
        int c2 = container.getObject("c2");
        assertEquals(123, c2);
        assertEquals(123, container.getObject(Integer.class));
        UserService c3 = container.getObject(UserService.class);
        assertTrue(c3 instanceof UserServiceImpl);
        assertTrue(((UserServiceImpl) c3).getUserDao() instanceof UserDaoImpl);
        assertTrue(container.getObject(UserService.class) instanceof UserServiceImpl);
        assertTrue(container.getObject(UserDao.class) instanceof UserDaoImpl);
    }
}
