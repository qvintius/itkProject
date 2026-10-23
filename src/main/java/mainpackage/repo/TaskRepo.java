package mainpackage.repo;

import mainpackage.domain.StatusTask;
import mainpackage.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    @Query("select t from Task t")
    public List<Task> findAllByFilter(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Task t set t.status= :status where t.id = :id")
    public void changeStatus(@Param("id") Long id, @Param("status") StatusTask status);



}
