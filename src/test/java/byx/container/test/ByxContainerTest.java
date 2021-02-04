package byx.container.test;

import byx.container.core.ByxContainer;
import byx.container.core.Container;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static byx.container.core.Component.*;

public class ByxContainerTest
{
    @Test
    public void test()
    {
        Container container = new ByxContainer();
        container.addComponent("c1", constructor(String.class, value("hello")));
        container.addComponent("c2", staticFactory(List.class, "of", value(1), value(2), value(3)));
        assertEquals("hello", container.getComponent("c1"));
        assertEquals(List.of(1, 2, 3), container.getComponent("c2"));
        assertThrows(RuntimeException.class, () -> container.addComponent("c3", null));
        assertThrows(RuntimeException.class, () -> container.getComponent("c3"));
    }
}
