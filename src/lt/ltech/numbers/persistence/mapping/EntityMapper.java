package lt.ltech.numbers.persistence.mapping;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public interface EntityMapper<E> {
    ContentValues toContentValues(E entity);

    E mapRow(Cursor cursor) throws SQLException;
}
