package common.module.jpa.baserepo.jdk;

import common.module.jpa.GeneralJpaRepo;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractBaseJpaRepoImpl<E, D, I> implements JpaRepositoryImplementation<E, I>, GeneralJpaRepo<E, D, I> {
    @Getter
    protected SimpleJpaRepository<E, I> simpleJpaRepository;

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
    @Transactional
    public <S extends E> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAllAndFlush(entities);
    }

    @Override
    @Transactional
    public void deleteInBatch(Iterable<E> entities) {
        simpleJpaRepository.deleteInBatch(entities);
    }

    @Override
    @Transactional
    public void deleteAllInBatch(Iterable entities) {
        simpleJpaRepository.deleteAllInBatch(entities);
    }

    @Override
    @Transactional
    public void deleteAllByIdInBatch(Iterable iterable) {
        simpleJpaRepository.deleteAllByIdInBatch(iterable);
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        simpleJpaRepository.deleteAllInBatch();
    }

    @Override
    public E getOne(I s) {
        return simpleJpaRepository.getOne(s);
    }

    @Override
    public E getById(I object) {
        return simpleJpaRepository.getById(object);
    }

    @Override
    public E getReferenceById(I object) {
        return simpleJpaRepository.getReferenceById(object);
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
        return findBy(example, queryFunction);
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

    @Override
    public boolean exists(Specification<E> spec) {
        return simpleJpaRepository.exists(spec);
    }

    @Override
    @Transactional
    public long delete(Specification<E> spec) {
        return simpleJpaRepository.delete(spec);
    }

    @Override
    public <S extends E, R> R findBy(Specification<E> spec, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return simpleJpaRepository.findBy(spec, queryFunction);
    }

    @Override
    @Transactional
    public <S extends E> S save(S entity) {
        return simpleJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public <S extends E> List<S> saveAll(Iterable<S> entities) {
        return simpleJpaRepository.saveAll(entities);
    }

    @Override
    public Optional<E> findById(I s) {
        return simpleJpaRepository.findById(s);
    }

    @Override
    public boolean existsById(I s) {
        return simpleJpaRepository.existsById(s);
    }

    @Override
    public List<E> findAll() {
        return simpleJpaRepository.findAll();
    }

    @Override
    public List<E> findAllById(Iterable<I> strings) {
        return findAllById(strings);
    }

    @Override
    public long count() {
        return simpleJpaRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(I s) {
        simpleJpaRepository.deleteById(s);
    }

    @Override
    @Transactional
    public void delete(E entity) {
        simpleJpaRepository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends E> entities) {
        simpleJpaRepository.deleteAll(entities);
    }

    @Override
    @Transactional
    public void deleteAll() {
        simpleJpaRepository.deleteAll();
    }

    @Override
    public List<E> findAll(Sort sort) {
        return simpleJpaRepository.findAll(sort);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return simpleJpaRepository.findAll(pageable);
    }
}
