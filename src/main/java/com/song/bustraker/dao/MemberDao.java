package com.song.bustraker.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.song.bustraker.dto.MemberDto;

@Repository
public interface MemberDao extends MongoRepository<MemberDto, String> {
    boolean existsByUsername(String username);
    MemberDto findByUsername(String username);
}
