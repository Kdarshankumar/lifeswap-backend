package com.lifeswapplus.server.repository;

import com.lifeswapplus.server.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUserId(Long userId);

    // Used by matching engine:
    List<Skill> findByUserIdAndType(Long userId, String type);

    // Find offers of a given skill name (optionally category could also be checked)
    List<Skill> findBySkillNameAndType(String skillName, String type);

    // Optional: find offers by skill name and category
    List<Skill> findBySkillNameAndCategoryAndType(String skillName, String category, String type);
}
