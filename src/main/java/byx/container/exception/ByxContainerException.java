package byx.container.exception;

/**
 * ByxContainer异常基类
 */
public class ByxContainerException extends RuntimeException
{
    public ByxContainerException()
    {

    }

    public ByxContainerException(Exception e)
    {
        super(e);
    }

    public ByxContainerException(String msg)
    {
        super(msg);
    }

    public ByxContainerException(String msg, Exception e)
    {
        super(msg, e);
    }
}
