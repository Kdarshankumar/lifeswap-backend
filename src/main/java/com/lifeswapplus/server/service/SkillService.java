package com.lifeswapplus.server.service;

import com.lifeswapplus.server.model.MatchResult;
import com.lifeswapplus.server.model.Skill;
import com.lifeswapplus.server.model.User;
import com.lifeswapplus.server.repository.SkillRepository;
import com.lifeswapplus.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    // -----------------------------------------------------------
    // ADD SKILL
    // -----------------------------------------------------------
    public Skill addSkill(Long userId, Skill skill) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        skill.setUser(user);

        // ✅ normalize type so DB is always clean
        if (skill.getType() != null) {
            skill.setType(skill.getType().toUpperCase());
        }

        return skillRepository.save(skill);
    }


    // -----------------------------------------------------------
// GET a skill by ID ✅ REQUIRED
// -----------------------------------------------------------
    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findById(id);
    }

    // -----------------------------------------------------------
// UPDATE skill ✅ REQUIRED
// -----------------------------------------------------------
    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }


    // -----------------------------------------------------------
    // GET all skills for a user
    // -----------------------------------------------------------
    public List<Skill> getSkillsByUser(Long userId) {
        return skillRepository.findByUserId(userId);
    }

    // -----------------------------------------------------------
    // DELETE skill
    // -----------------------------------------------------------
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    // ===========================================================
    // ✅ ✅ ✅ FINAL MATCHING ENGINE (STABLE & CORRECT)
    // ===========================================================
    public List<MatchResult> findMatches(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ MUST MATCH FRONTEND EXACTLY: "want"
        List<Skill> wantSkills =
                skillRepository.findByUserIdAndType(userId, "want");

        if (wantSkills.isEmpty()) return Collections.emptyList();

        List<MatchResult> results = new ArrayList<>();

        for (Skill want : wantSkills) {

            // ✅ MUST MATCH FRONTEND EXACTLY: "offer"
            List<Skill> offers =
                    skillRepository.findBySkillNameAndType(
                            want.getSkillName(),
                            "offer"
                    );

            for (Skill offer : offers) {

                // ✅ ABSOLUTE SELF-MATCH BLOCK
                if (offer.getUser().getId().equals(userId)) {
                    continue;
                }

                int score = computeScore(
                        want.getLevel(),
                        offer.getLevel(),
                        want.getCategory(),
                        offer.getCategory()
                );

                MatchResult match = new MatchResult(
                        offer.getUser().getId(),
                        offer.getUser().getName(),
                        offer.getUser().getEmail(),
                        offer.getId(),
                        offer.getSkillName(),
                        offer.getLevel(),
                        offer.getCategory(),
                        want.getSkillName(),
                        score
                );

                results.add(match);
            }
        }

        // ✅ Remove duplicate matches
        Map<String, MatchResult> unique = new HashMap<>();
        for (MatchResult m : results) {
            String key = m.getUserId() + ":" + m.getTheirSkillId();
            if (!unique.containsKey(key)
                    || unique.get(key).getScore() < m.getScore()) {
                unique.put(key, m);
            }
        }

        List<MatchResult> finalList = new ArrayList<>(unique.values());

        // ✅ Sort best matches first
        finalList.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        return finalList;
    }

    // -----------------------------------------------------------
    // SCORE CALCULATION
    // -----------------------------------------------------------
    private int computeScore(String wantLevel, String offerLevel,
                             String wantCategory, String offerCategory) {

        int score = 50;

        if (wantCategory != null && offerCategory != null
                && wantCategory.equalsIgnoreCase(offerCategory)) {
            score += 20;
        }

        if (wantLevel != null && offerLevel != null) {
            if (wantLevel.equalsIgnoreCase(offerLevel)) score += 20;
            else if (offerLevel.equalsIgnoreCase("Advanced")) score += 10;
        }

        return Math.min(score, 100);
    }
}

