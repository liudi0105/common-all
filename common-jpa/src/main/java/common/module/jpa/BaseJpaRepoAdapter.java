package common.module.jpa;

public interface BaseJpaRepoAdapter {

    String getTableName(Class<?> entityClass);

}
