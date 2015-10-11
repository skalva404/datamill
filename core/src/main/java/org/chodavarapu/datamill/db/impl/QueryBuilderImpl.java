package org.chodavarapu.datamill.db.impl;

import com.google.common.base.Joiner;
import org.chodavarapu.datamill.db.*;
import rx.Observable;

import java.util.Arrays;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class QueryBuilderImpl implements QueryBuilder {
    private static final String SQL_SELECT = "SELECT ";
    private static final String SQL_EQ = " = ";
    private static final String SQL_FROM = " FROM ";
    private static final String SQL_PARAMETER_PLACEHOLDER = "?";
    private static final String SQL_WHERE = " WHERE ";

    private class SelectQuery implements SelectBuilder, WhereBuilder, ConditionBuilder {
        private final StringBuilder query = new StringBuilder();

        public SelectQuery() {
            query.append(SQL_SELECT);
            query.append('*');
        }

        public SelectQuery(Iterable<String> columns) {
            query.append(SQL_SELECT);
            query.append(Joiner.on(',').join(columns));
        }

        @Override
        public Observable<Row> all() {
            return QueryBuilderImpl.this.query(query.toString());
        }

        @Override
        public <T> Observable<Row> eq(String column, T value) {
            query.append(column);
            query.append(SQL_EQ);
            query.append(SQL_PARAMETER_PLACEHOLDER);
            return QueryBuilderImpl.this.query(query.toString(), value);
        }

        @Override
        public WhereBuilder from(String table) {
            query.append(SQL_FROM);
            query.append(table);
            return this;
        }

        @Override
        public ConditionBuilder where() {
            query.append(SQL_WHERE);
            return this;
        }
    }

    protected abstract Observable<Row> query(String query);
    protected abstract Observable<Row> query(String query, Object... parameters);

    @Override
    public SelectBuilder select(String column) {
        return select(Arrays.asList(column));
    }

    @Override
    public SelectBuilder select(String... columns) {
        return select(Arrays.asList(columns));
    }

    @Override
    public SelectBuilder select(Iterable<String> columns) {
        return new SelectQuery(columns);
    }

    @Override
    public SelectBuilder selectAll() {
        return new SelectQuery();
    }
}
