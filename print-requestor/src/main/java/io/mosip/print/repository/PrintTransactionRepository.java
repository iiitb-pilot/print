package io.mosip.print.repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.print.entity.PrintTransactionEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("printTransactionRepository")
public interface PrintTransactionRepository extends BaseRepository<PrintTransactionEntity, String>, JpaSpecificationExecutor<PrintTransactionEntity> {

    @Modifying(clearAutomatically = true)
    @Query("update PrintTransactionEntity p set p.statusComment= :comment where p.regId = :rid")
    void updateStatusComment(@Param("comment") String comment, @Param("rid") String rid);

    List<PrintTransactionEntity> findAllByIsDeletedAndRegIdIn(Boolean isDeleted, List<String> registrationIds);
}
