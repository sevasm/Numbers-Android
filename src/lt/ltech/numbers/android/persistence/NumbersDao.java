package lt.ltech.numbers.android.persistence;

import java.util.ArrayList;
import java.util.List;

import lt.ltech.numbers.android.log.Logger;
import lt.ltech.numbers.android.persistence.mapping.EntityMapper;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class NumbersDao {
    private final Logger logger = new Logger(this.getClass().getName());
    private DatabaseHelper pm;

    public NumbersDao(Context context) {
        this.pm = new DatabaseHelper(context);
    }

    public synchronized <E> List<E> findAll(EntityMapper<E> mapper) {
        List<E> entities = new ArrayList<E>();
        Cursor c = null;
        try {
            SQLiteDatabase database = this.pm.getWritableDatabase();
            c = database.query(this.getTableName(), this.getColumns(), null,
                    null, null, null, null);
            while (c.moveToNext()) {
                E entity = mapper.mapRow(c);
                this.logger.d("Retrieved %s", entity);
                entities.add(entity);
            }
            this.logger.d("Retrieved %d entities", entities.size());
        } catch (SQLException e) {
            this.logger.w("Failed to find all entities");
            throw e;
        } finally {
            if (c != null) {
                c.close();
            }
            this.pm.close();
        }
        return entities;
    }

    public synchronized <E> E findById(Long id, EntityMapper<E> mapper) {
        E entity = null;
        Cursor c = null;
        try {
            SQLiteDatabase database = this.pm.getWritableDatabase();
            c = database.query(this.getTableName(), this.getColumns(),
                    "id = ?", new String[] { id.toString() }, null, null, null);
            c.moveToFirst();
            entity = mapper.mapRow(c);
            this.logger.d("Retrieved %s with ID %d", entity, id);
        } catch (SQLException e) {
            this.logger.w("Found entity with ID %d", id);
            throw e;
        } finally {
            if (c != null) {
                c.close();
            }
            this.pm.close();
        }
        return entity;
    }

    public synchronized <E> Long insert(E entity, EntityMapper<E> mapper) {
        Long id = null;
        try {
            SQLiteDatabase database = this.pm.getWritableDatabase();
            id = database.insert(this.getTableName(), null,
                    mapper.toContentValues(entity));
            this.logger.d("Inserted %s with ID %d", entity, id);
        } catch (SQLException e) {
            this.logger.w("Failed to insert %s into the database", entity);
            throw e;
        } finally {
            this.pm.close();
        }
        return id;
    }

    public synchronized <E> void deleteById(Long id) {
        try {
            SQLiteDatabase database = this.pm.getWritableDatabase();
            database.delete(this.getTableName(), "id = ?",
                    new String[] { id.toString() });
            this.logger.d("Deleted entity with ID %d", id);
        } catch (SQLException e) {
            this.logger.w("Failed to delete entity with ID %d", id);
            throw e;
        } finally {
            this.pm.close();
        }
    }

    public abstract String getTableName();

    public abstract String[] getColumns();
}
