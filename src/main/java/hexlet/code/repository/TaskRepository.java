package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {
    Optional<Task> findByName(String name);
}
