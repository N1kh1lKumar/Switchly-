package live.switchly.api.repository;

import live.switchly.api.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlagRepository extends JpaRepository<Flag, UUID> {

    List<Flag> findByProjectId(UUID projectId);

    boolean existsByProjectIdAndKey(UUID projectId, String key);
}