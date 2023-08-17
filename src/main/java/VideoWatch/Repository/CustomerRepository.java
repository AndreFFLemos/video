package VideoWatch.Repository;

import VideoWatch.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<List<Customer>> findByFirstName(String s);

    Optional<List<Customer>> findByLastName(String s);

    Optional<Customer> findByEmail(String s);

    void deleteByEmail(String email);
}
