//package dev.petproject.repository;
//
//import dev.petproject.domain.Token;
//import jakarta.transaction.Transactional;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@Transactional
//public interface TokenRepository extends JpaRepository<Token, Integer> {
//
//    @Query("""
//            SELECT t FROM Token t INNER JOIN User u ON t.id = u.id
//            WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)""")
//    List<Token> findAllValidTokenByUserId(Integer userId);
//
//    void deleteTokenByUserId(Integer id);
//
//    Optional<Token> findByToken(String token);
//
//}
