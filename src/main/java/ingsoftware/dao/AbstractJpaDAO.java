package ingsoftware.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJpaDAO<T, ID> implements BaseDAO<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public AbstractJpaDAO() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T save(T entity) {
        // Implementazione esplicita con persist/merge
        if (isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        // Query JPQL esplicita
        TypedQuery<T> query = entityManager.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id",
                entityClass);
        query.setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        // Query JPQL esplicita
        TypedQuery<T> query = entityManager.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                entityClass);
        return query.getResultList();
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ?
                entity : entityManager.merge(entity));
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public boolean existsById(ID id) {
        // Query JPQL esplicita con COUNT
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id",
                Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public long count() {
        // Query JPQL esplicita per contare tutte le entità
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e",
                Long.class);
        return query.getSingleResult();
    }

    // Metodo helper per determinare se un'entità è nuova
    protected abstract boolean isNew(T entity);
}
