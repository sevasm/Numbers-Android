package lt.ltech.numbers.persistence;

public abstract class BasicEntity {
    public abstract Long getId();

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicEntity)) {
            return false;
        }
        BasicEntity entity = (BasicEntity) o;
        return this.getId().equals(entity.getId());
    }
}
