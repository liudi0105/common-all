package common.module.jpa.baserepo;

import com.google.common.collect.Lists;
import common.module.dto.AppPageParam;
import common.module.jpa.*;
import common.module.jpa.baserepo.jdk.AbstractBaseJpaRepoImpl;
import common.module.jpa.data.Converter;
import common.module.util.AppBeans;
import common.module.util.AppJsons;
import common.module.util.AppReflections;
import common.module.util.errors.AppError;
import common.module.util.errors.AppWarning;
import common.module.util.model.SerializableFunction;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class BaseJpaRepositoryImpl<E, D, I> extends AbstractBaseJpaRepoImpl<E, D, I> implements GeneralJpaRepo<E, D, I> {

    private QueryManager queryManager;

    @Getter
    private String tableName;

    @Getter
    private final JpaEntityInformation<E, I> entityInformation;

    @Getter
    private final Class<E> entityClass;

    @Getter
    private final AppEntityManager<E> appEntityManager;

    @Getter
    protected final Class<D> dtoClass;

    private final Class<?> repoClass;

    private final Converter<E, D> converter;

    private final Boolean useBeanUtils;

    public BaseJpaRepositoryImpl(JpaEntityInformation<E, I> entityInformation,
                                 QueryManager queryManager,
                                 BaseJpaRepoAdapter baseJpaRepoAdapter,
                                 AppEntityManager appEntityManager,
                                 Class<?> repositoryInterface,
                                 Converter<E, D> converter, SimpleJpaRepository<E, I> simpleJpaRepository
    ) {
        this.entityClass = entityInformation.getJavaType();
        this.entityInformation = entityInformation;
        this.appEntityManager = appEntityManager;
        this.tableName = baseJpaRepoAdapter.getTableName(entityClass);
        this.queryManager = queryManager;
        this.repoClass = repositoryInterface;
        this.simpleJpaRepository = simpleJpaRepository;
        this.converter = converter;
        useBeanUtils = repositoryInterface.isAnnotationPresent(BeanConverter.class);
        ParameterizedType clazz = (ParameterizedType) repositoryInterface.getGenericInterfaces()[0];
        dtoClass = (Class<D>) clazz.getActualTypeArguments()[1];
    }

    public E e(D d) {
        if (converter != null) {
            return converter.toE(d);
        } else if (useBeanUtils) {
            return AppBeans.convert(d, getEntityClass());
        } else {
            return AppJsons.convert(d, getEntityClass());
        }
    }

    public D d(E e) {
        if (converter != null) {
            return converter.toD(e);
        } else if (useBeanUtils) {
            return AppBeans.convert(e, getDtoClass());
        } else {
            return AppJsons.convert(e, getDtoClass());
        }
    }

    public List<D> d(Collection<E> es) {
        return es.stream().map(this::d).collect(Collectors.toList());
    }

    public List<E> e(Collection<D> d) {
        return d.stream().map(this::e).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<E> updatePoBatch(Collection<E> collection) {
        if (collection.isEmpty()) {
            return Lists.newArrayList();
        }
        if (collection.stream().anyMatch(v -> entityInformation.getId(v) == null)) {
            throw new AppWarning("batch update blank ids found");
        }

        Map<I, E> collect = collection.stream()
                .collect(Collectors.toMap(entityInformation::getId, Function.identity()));

        List<E> collect1 = simpleJpaRepository.findAllById(collect.keySet());

        collect1.forEach(v -> {
            E d = collect.get(entityInformation.getId(v));
            if (d != null) {
                AppBeans.copyNonNullField(d, v);
            }
        });

        return simpleJpaRepository.saveAll(collect1);
    }

    @Transactional
    public List<D> updateBatch(Collection<D> dcs) {
        return d(updatePoBatch(e(dcs)));
    }

    @Override
    public D getOneById(I id) {
        if (id == null) {
            throw new AppWarning("getById with null id");
        }
        return simpleJpaRepository.findById(id)
                .map(this::d)
                .orElseThrow(() -> new AppWarning(entityClass.getSimpleName() + "'s id not found: id=" + id));
    }

    @Override
    public List<D> listByIds(Collection<I> strings) {
        if (CollectionUtils.isEmpty(strings)) {
            return Lists.newArrayList();
        }
        return simpleJpaRepository.findAllById(strings)
                .stream().map(this::d).collect(Collectors.toList());
    }

    @Override
    public Optional<D> findOneById(I s) {
        return simpleJpaRepository.findById(s).map(this::d);
    }

    @Override
    @Transactional
    public D saveOrUpdate(D d) {
        return d(simpleJpaRepository.save(e(d)));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int delete(UnaryOperator<ConditionBuilder<E>> func) {
        ConditionBuilder<E> apply = func.apply(cb());
        if (apply.getType().equals(ConditionBuilder.Type.EMPTY_CONDITION)) {
            throw new AppWarning("Delete Condition Error");
        }
        List<D> list = list(apply);
        Set<I> collect1 = id(list);
        simpleJpaRepository.deleteAllById(collect1);
        return collect1.size();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <V> int deleteIn(SerializableFunction<E, V> func, List<V> value) {
        if (CollectionUtils.isEmpty(value)) {
            return 0;
        }
        List<D> collect = listIn(func, value);
        return deleteBatch(collect);
    }

    public Set<I> eid(Collection<E> i) {
        return i.stream().map(this::eid).collect(Collectors.toSet());
    }

    public Set<I> id(Collection<D> i) {
        return eid(e(i));
    }

    public I eid(E i) {
        return entityInformation.getId(i);
    }

    public I id(D i) {
        return eid(e(i));
    }

    @Transactional
    @Override
    public int deleteBatch(Collection<D> entities) {
        simpleJpaRepository.deleteAllById(id(entities));
        return entities.size();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <V> int deleteEq(SerializableFunction<E, V> func, V value) {
        if (value == null) {
            return 0;
        }
        List<D> collect = listEq(func, value);
        if (collect.isEmpty()) {
            return 0;
        }
        return deleteBatch(collect);
    }

    private ConditionBuilder<E> cb() {
        return new ConditionBuilder<>();
    }

    private QueryBuilder<E> qb() {
        return new QueryBuilder<>();
    }

    @Override
    public void deleteOne(D collection) {
        simpleJpaRepository.deleteById(id(collection));
    }

    public <V> Optional<D> findEq(SerializableFunction<E, V> func, V value) {
        if (value == null) {
            return Optional.empty();
        }
        return find(cb().eq(func, value));
    }

    @Override
    public <V> Optional<E> findPoEq(SerializableFunction<E, V> func, V value) {
        if (value == null) {
            return Optional.empty();
        }
        return findPo(cb().eq(func, value));
    }

    @Override
    public <V> E getPoEq(SerializableFunction<E, V> func, V value) {
        return findPoEq(func, value)
                .orElseThrow(() -> new AppError(entityClass.getSimpleName() + " not found: " + AppReflections.getFieldName(func) + "=" + value));
    }

    public <V> D getEq(SerializableFunction<E, V> func, V value) {
        return findEq(func, value)
                .orElseThrow(() -> new AppError(entityClass.getSimpleName() + " not found: " + AppReflections.getFieldName(func) + "=" + value));
    }

    public <V> List<D> listIn(SerializableFunction<E, V> func, Collection<V> conditions) {
        return listIn(toName(func), conditions);
    }

    @Override
    public <V> List<E> listPoEq(SerializableFunction<E, V> func, V value) {
        return listPoEq(toName(func), value);
    }

    public <V> List<D> listEq(SerializableFunction<E, V> func, V value) {
        return d(listPoEq(func, value));
    }

    public <V> long count(SerializableFunction<E, V> func, V value) {
        return count(cb().eq(func, value));
    }

    protected List<D> listIn(String func, Collection<?> conditions) {
        if (CollectionUtils.isEmpty(conditions))
            return Lists.newArrayList();

        return list(cb().in(func, conditions));
    }

    public <V> List<E> listPoEq(String key, V value) {
        if (value == null) {
            return Lists.newArrayList();
        }
        return listPo(cb().eq(key, value));
    }

    private <V> List<D> listEq(String key, V value) {
        return d(listPoEq(key, value));
    }

    protected String toName(SerializableFunction<E, ?> function) {
        return AppReflections.getFieldName(function);
    }

    protected QueryBuilder<E> preSelect(QueryBuilder<E> query) {
        return query;
    }

    public Optional<D> find(UnaryOperator<ConditionBuilder<E>> func) {
        return find(func.apply(cb()));
    }

    @Override
    public Optional<E> findPo(ConditionBuilder<E> condition) {
        return findPo(condition.toQB());
    }

    private Optional<D> find(ConditionBuilder<E> condition) {
        return find(condition.toQB());
    }

    @Override
    public Optional<E> findPo(QueryBuilder<E> condition) {
        preSelect(condition);
        return simpleJpaRepository.findOne(condition.toSpecification());
    }

    private Optional<D> find(QueryBuilder<E> condition) {
        preSelect(condition);
        return simpleJpaRepository.findOne(condition.toSpecification()).map(this::d);
    }

    private PageRequest pageCondition(Integer pageIndex, Integer pageSize) {
        if (pageIndex == null || pageIndex < 1) {
            throw new AppError("pageIndex 参数不能为 null 或小于 1");
        }
        if (pageSize == null || pageSize < 1) {
            throw new AppError("pageSize 参数不能为 null 或小于 1");
        }
        return PageRequest.of(pageIndex - 1, pageSize);
    }

    @Override
    public AppPageResult<D> pageQuery(AppPageParam pageParam, UnaryOperator<QueryBuilder<E>> condition) {
        return page(pageParam.getPageIndex(), pageParam.getPageSize(), condition.apply(qb()));
    }

    public AppPageResult<D> page(Integer pageIndex, Integer pageSize, UnaryOperator<ConditionBuilder<E>> condition) {
        return page(pageIndex, pageSize, condition.apply(cb()).toQB());
    }

    public AppPageResult<D> page(Integer pageIndex, Integer pageSize, QueryBuilder<E> condition) {
        condition = preSelect(condition);
        Page<E> page = simpleJpaRepository.findAll(condition.toSpecification(), pageCondition(pageIndex, pageSize));
        return AppPageResult.of(page).map(this::d);
    }

    public List<D> list(UnaryOperator<ConditionBuilder<E>> func) {
        return list(func.apply(cb()));
    }

    public List<D> listQuery(UnaryOperator<QueryBuilder<E>> q) {
        QueryBuilder<E> apply = q.apply(new QueryBuilder<>());
        preSelect(apply);
        return list(apply);
    }

    public List<D> list(QueryBuilder<E> q) {
        preSelect(q);
        return d(simpleJpaRepository.findAll(q.toSpecification()));
    }

    public List<E> listPo(QueryBuilder<E> q) {
        preSelect(q);
        return simpleJpaRepository.findAll(q.toSpecification());
    }

    List<D> list(ConditionBuilder<E> conditionBuilder) {
        return list(conditionBuilder.toQB());
    }

    List<E> listPo(ConditionBuilder<E> conditionBuilder) {
        return listPo(conditionBuilder.toQB());
    }

    public long count(UnaryOperator<QueryBuilder<E>> condition) {
        return count(condition.apply(qb()));
    }

    long count(QueryBuilder<E> condition) {
        preSelect(condition);
        return simpleJpaRepository.count(condition.toSpecification());
    }

    long count(ConditionBuilder<E> condition) {
        return count(condition.toQB());
    }

    @Transactional
    @Override
    public List<D> createOrUpdateBatch(Collection<D> entities) {
        if (entities.isEmpty()) {
            return Lists.newArrayList();
        }
        return d(simpleJpaRepository.saveAll(e(entities)));
    }

    @Override
    public D createOrUpdate(D entity) {
        I id = id(entity);
        if (id != null) {
            return update(entity);
        } else {
            return create(entity);
        }
    }

    @Transactional
    @Override
    public List<D> createBatch(Collection<D> entities) {
        return d(simpleJpaRepository.saveAll(e(entities)));
    }

    @Transactional
    @Override
    public D create(D entity) {
        I id = id(entity);
        if (id != null) {
            throw new AppWarning("create failed, entity with id");
        }
        return d(simpleJpaRepository.save(e(entity)));
    }

    @Transactional
    @Override
    public D update(D entity) {
        I id = id(entity);
        if (id == null) {
            throw new AppError("entity id to update is null");
        }
        E e = simpleJpaRepository.findById(id)
                .orElseThrow(() -> new AppError("update failed, data not found"));
        AppBeans.copyNonNullField(e(entity), e);
        return d(simpleJpaRepository.save(e));
    }

    public <R> R queryOne(SqlBuilder oldSqlBuilder, Class<R> resultClass) {
        return queryManager.queryOne(oldSqlBuilder, resultClass);
    }

    public <R> List<R> queryList(SqlBuilder oldSqlBuilder, Class<R> resultClass) {
        return queryManager.queryList(oldSqlBuilder, resultClass);
    }

    public <R> AppPageResult<R> queryPage(SqlBuilder oldSqlBuilder, Integer pageIndex, Integer pageSize, Class<R> resultClass) {
        return queryManager.queryPage(oldSqlBuilder, resultClass, pageIndex, pageSize);
    }

    public boolean exists(UnaryOperator<ConditionBuilder<E>> spec) {
        long count = count(spec.apply(cb()));
        return count > 0;
    }

    public <V> boolean exists(SerializableFunction<E, V> supplier, V value) {
        return exists(v -> v.eq(supplier, value));
    }

    @Transactional
    public void delete(E entity) {
        simpleJpaRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends I> is) {
        simpleJpaRepository.deleteAllById(is);
    }

    @Override
    public E getOne(I i) {
        return simpleJpaRepository.getOne(i);
    }

    @Override
    public E getById(I i) {
        return simpleJpaRepository.getById(i);
    }

    @Override
    public E getReferenceById(I i) {
        return simpleJpaRepository.getReferenceById(i);
    }

    @Override
    public Optional<E> findById(I i) {
        return simpleJpaRepository.findById(i);
    }

    @Override
    public boolean existsById(I i) {
        return simpleJpaRepository.existsById(i);
    }

    @Override
    public List<E> findAllById(Iterable<I> is) {
        return simpleJpaRepository.findAllById(is);
    }

    @Override
    public void deleteById(I i) {
        simpleJpaRepository.deleteById(i);
    }
}
