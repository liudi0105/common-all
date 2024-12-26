package common.module.jpa;

import common.module.dto.AppPageParam;
import common.module.dto.BaseDTO;
import common.module.jpa.condition.ConditionBuilder;
import common.module.jpa.condition.QueryBuilder;
import common.module.util.model.SerializableFunction;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@NoRepositoryBean
public interface AppJpaRepo<E extends BaseJpaEntity, D extends BaseDTO> extends Repository<E, String> {

    JpaEntityInformation<E, String> getEntityInformation();

    Class<E> getEntityClass();

    Class<D> getDtoClass();

    D getByOneId(String id);

    List<D> listByIds(Collection<String> strings);

    Optional<D> findOneById(String s);

    List<E> updatePoBatch(Collection<E> collection);

    @Transactional
    List<D> updateBatch(Collection<D> entities);

    @Transactional
    int deleteBatch(Collection<D> entities);

    void delete(D collection);

    void deleteAll(Collection<D> collection);

    AppPageResult<D> pageQuery(AppPageParam pageParam, UnaryOperator<QueryBuilder<E>> condition);

    @Transactional
    List<D> createOrUpdateBatch(Collection<D> entities);

    D createOrUpdate(D entity);

    @Transactional
    List<D> createBatch(Collection<D> entities);

    <V> int deleteIn(SerializableFunction<E, V> func, List<V> value);

    int delete(UnaryOperator<ConditionBuilder<E>> func);

    <V> int deleteEq(SerializableFunction<E, V> func, V value);

    <V> Optional<D> findEq(SerializableFunction<E, V> func, V value);

    <V> D getEq(SerializableFunction<E, V> func, V value);

    <V> List<D> listIn(SerializableFunction<E, V> func, Collection<V> conditions);

    <V> List<E> listPoEq(SerializableFunction<E, V> func, V value);

    <V> List<D> listEq(SerializableFunction<E, V> func, V value);

    <V> long count(SerializableFunction<E, V> func, V value);

    Optional<D> find(UnaryOperator<ConditionBuilder<E>> func);

    AppPageResult<D> page(Integer pageIndex, Integer pageSize, UnaryOperator<ConditionBuilder<E>> condition);

    AppPageResult<D> page(Integer pageIndex, Integer pageSize, QueryBuilder<E> condition);

    List<D> list(UnaryOperator<ConditionBuilder<E>> func);

    List<D> listQuery(UnaryOperator<QueryBuilder<E>> q);

    long count(UnaryOperator<QueryBuilder<E>> condition);

    D create(D entity);

    D saveOrUpdate(D d);

    D update(D entity);

    <R> R queryOne(SqlBuilder oldSqlBuilder, Class<R> resultClass);

    <R> List<R> queryList(SqlBuilder oldSqlBuilder, Class<R> resultClass);

    <R> AppPageResult<R> queryPage(SqlBuilder oldSqlBuilder, Integer pageIndex, Integer pageSize, Class<R> resultClass);

    boolean exists(UnaryOperator<ConditionBuilder<E>> spec);

    <V> boolean exists(SerializableFunction<E, V> supplier, V value);
}
