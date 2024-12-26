package common.module.jpa;

import common.module.util.errors.AppError;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class SimpleJpaRepositoryAdapter<E, I> implements JpaRepositoryImplementation<E, I> {

    private final SimpleJpaRepository<E, I> simpleJpaRepository;

    public SimpleJpaRepositoryAdapter(SimpleJpaRepository<E, I> simpleJpaRepository) {
        this.simpleJpaRepository = simpleJpaRepository;
    }

    @Transactional
    public void deleteAllById(Iterable<? extends I> is) {
        List<E> allById = findAllById((Iterable<I>) is);
        simpleJpaRepository.deleteAll(allById);
    }

    @Transactional
    public void deleteAll(Iterable<? extends E> entities) {
        simpleJpaRepository.deleteAll(entities);
    }

    @Transactional
    public void deleteAll() {
        simpleJpaRepository.deleteAll();
    }

    @Override
    @Transactional
    public void flush() {
        simpleJpaRepository.flush();
    }

    @Override
    @Transactional
    public <S extends E> S saveAndFlush(S entity) {
        return simpleJpaRepository.saveAndFlush(entity);
    }

    @Override
    public void deleteInBatch(Iterable<E> entities) {
        simpleJpaRepository.deleteInBatch(entities);
    }

    @Transactional
    public <S extends E> List<S> saveAllAndFlush(Iterable<S> entities) {
        List<S> s = simpleJpaRepository.saveAll(entities);
        flush();
        return s;
    }

    @Deprecated
    public void deleteAllInBatch(Iterable<E> entities) {
        simpleJpaRepository.deleteInBatch(entities);
    }

    public void deleteAllByIdInBatch(Iterable<I> is) {
        List<E> allById = findAllById(is);
        simpleJpaRepository.deleteInBatch(allById);
    }

    @Override
    @Deprecated
    public void deleteAllInBatch() {
        simpleJpaRepository.deleteAllInBatch();
    }

    @Override
    public E getOne(I i) {
        return simpleJpaRepository.getOne(i);
    }

    public E getById(I i) {
        return simpleJpaRepository.findById(i).orElseThrow(() -> new AppError("data not found, id=" + i));
    }

    public E getReferenceById(I i) {
        return getById(i);
    }

    @Override
    public <S extends E> Optional<S> findOne(Example<S> example) {
        return simpleJpaRepository.findOne(example);
    }

    @Override
    public <S extends E> List<S> findAll(Example<S> example) {
        return simpleJpaRepository.findAll(example);
    }

    @Override
    public <S extends E> List<S> findAll(Example<S> example, Sort sort) {
        return simpleJpaRepository.findAll(example, sort);
    }

    @Override
    public <S extends E> Page<S> findAll(Example<S> example, Pageable pageable) {
        return simpleJpaRepository.findAll(example, pageable);
    }

    @Override
    public <S extends E> long count(Example<S> example) {
        return simpleJpaRepository.count(example);
    }

    @Override
    public <S extends E> boolean exists(Example<S> example) {
        return simpleJpaRepository.exists(example);
    }

    @Override
    public <S extends E, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return simpleJpaRepository.findBy(example, queryFunction);
    }

    @Override
    public Optional<E> findOne(Specification<E> spec) {
        return simpleJpaRepository.findOne(spec);
    }

    @Override
    public List<E> findAll(Specification<E> spec) {
        return simpleJpaRepository.findAll(spec);
    }

    @Override
    public Page<E> findAll(Specification<E> spec, Pageable pageable) {
        return simpleJpaRepository.findAll(spec, pageable);
    }

    @Override
    public List<E> findAll(Specification<E> spec, Sort sort) {
        return simpleJpaRepository.findAll(spec, sort);
    }

    @Override
    public long count(Specification<E> spec) {
        return simpleJpaRepository.count(spec);
    }

    public boolean exists(Specification<E> spec) {
        return count(spec) > 0;
    }

    public long delete(Specification<E> spec) {
        List<E> all = findAll(spec);
        deleteAll(all);
        return all.size();
    }

    public <S extends E, R> R findBy(Specification<E> spec, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new NotImplementedException();
    }

    @Override
    @Transactional
    public <S extends E> List<S> saveAll(Iterable<S> entities) {
        return simpleJpaRepository.saveAll(entities);
    }

    public Optional<E> findById(I i) {
        return simpleJpaRepository.findById(i);
    }

    public boolean existsById(I i) {
        return simpleJpaRepository.existsById(i);
    }

    @Override
    public List<E> findAll() {
        return simpleJpaRepository.findAll();
    }

    @Override
    public List<E> findAllById(Iterable<I> is) {
        return simpleJpaRepository.findAllById(is);
    }

    public long count() {
        return simpleJpaRepository.count();
    }

    @Transactional
    @Deprecated
    public void deleteById(I i) {
        simpleJpaRepository.deleteById(i);
    }

    @Transactional
    @Deprecated
    public void delete(E entity) {
        simpleJpaRepository.delete(entity);
    }

    @Override
    public List<E> findAll(Sort sort) {
        return simpleJpaRepository.findAll(sort);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return simpleJpaRepository.findAll(pageable);
    }

    @Override
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        simpleJpaRepository.setRepositoryMethodMetadata(crudMethodMetadata);
    }
}
