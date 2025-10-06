package ru.school21.TicTacToe.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.school21.TicTacToe.domain.model.Game;
import ru.school21.TicTacToe.dto.LeaderBoardDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends CrudRepository<Game, UUID> {
    Optional<Game> findById(UUID uuid);
    @Query("SELECT new ru.school21.TicTacToe.dto.LeaderBoardDTO(" +
            "p.id, " +
            "p.username, " +
            "SUM(CASE WHEN g.status LIKE '%Победил игрок %' AND g.status LIKE CONCAT('%', p.username, '%') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (g.status LIKE '%Победил игрок %' AND g.status NOT LIKE CONCAT('%', p.username, '%')) OR g.status = 'Компьютер' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN g.status = 'Ничья' THEN 1 ELSE 0 END), " +
            "CASE " +
            "  WHEN SUM(CASE WHEN (g.status LIKE '%Победил игрок %' AND g.status NOT LIKE CONCAT('%', p.username, '%')) OR g.status = 'Компьютер' THEN 1 ELSE 0 END) = 0 " +
            "  THEN 100.0 " +
            "  ELSE (SUM(CASE WHEN g.status LIKE '%Победил игрок %' AND g.status LIKE CONCAT('%', p.username, '%') THEN 1 ELSE 0 END) * 100.0 / " +
            "       NULLIF(SUM(CASE WHEN g.status LIKE '%Победил игрок %' AND g.status LIKE CONCAT('%', p.username, '%') THEN 1 ELSE 0 END) + " +
            "              SUM(CASE WHEN (g.status LIKE '%Победил игрок %' AND g.status NOT LIKE CONCAT('%', p.username, '%')) OR g.status = 'Компьютер' THEN 1 ELSE 0 END), 0)) " +
            "END) " +
            "FROM Game g " +
            "JOIN g.firstPerson p " +
            "WHERE g.status NOT IN ('в ожидании', 'В процессе') " +
            "GROUP BY p.id, p.username " +
            "ORDER BY 6 DESC")
    List<LeaderBoardDTO> findLeaderBoard();

    @Query("select g from Game g where g.status != 'в ожидании' and g.status != 'В процессе'")
    List<Game> findAllCompleteGames();
}
