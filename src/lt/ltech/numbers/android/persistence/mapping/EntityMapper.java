package lt.ltech.numbers.android.persistence.mapping;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

/**
 * Implementations of this interface are capable of creating entities from
 * ContentValues objects and can read a cursor to produce entities.
 * @author Severinas Monkevicius
 * @param <E> the type of entity this mapper operates on.
 */
public interface EntityMapper<E> {
    ContentValues toContentValues(E entity);

    E mapRow(Cursor cursor) throws SQLException;
}
