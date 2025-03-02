package modulumsu;

abstract class BaseEntity 
{
    private final String id;

    protected BaseEntity(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
}