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

    public synchronized <E> List<E> findBySelection(EntityMapper<E> mapper,
            String selection, String... selectionArgs) {
        List<E> entities = new ArrayList<E>();
        Cursor c = null;
        try {
            SQLiteDatabase database = this.pm.getWritableDatabase();
            c = database.query(this.getTableName(), this.getColumns(),
                    selection, selectionArgs, null, null, null);
            while (c.moveToNext()) {
                E entity = mapper.mapRow(c);
                this.logger.d("Retrieved %s", entity);
                entities.add(entity);
            }
            this.logger.d("Retrieved %d entities", entities.size());
        } catch (SQLException e) {
            this.logger.w("Failed to find entities");
            throw e;
        } finally {
            if (c != null) {
                c.close();
            }
            this.pm.close();
        }
        return entities;
    }

    public synchronized <E> List<E> findAll(EntityMapper<E> mapper) {
        return this.findBySelection(mapper, null, (String[]) null);
    }

    public synchronized <E> E findById(Long id, EntityMapper<E> mapper) {
        List<E> list = this.findBySelection(mapper, "id = ?", id.toString());
        E entity = null;
        if (list.size() > 0) {
            entity = list.get(0);
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

    public synchronized <E> void update(E entity, Long id,
            EntityMapper<E> mapper) {
        try {
            SQLiteDatabase database = this.pm.getWritableDatabase();
            database.update(this.getTableName(),
                    mapper.toContentValues(entity), "id = ?",
                    new String[] { id.toString() });
            this.logger.d("Updated %s with ID %d", entity, id);
        } catch (SQLException e) {
            this.logger.w("Failed to update %s", entity);
            throw e;
        } finally {
            this.pm.close();
        }
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
