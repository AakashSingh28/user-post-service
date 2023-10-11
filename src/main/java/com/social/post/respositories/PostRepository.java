package com.social.post.respositories;
import com.social.post.entities.UserPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface PostRepository extends MongoRepository<UserPost, Long> {
    @Query("{ 'userId': ?0, 'createdOn': { $gte: ?1 } }")
    List<UserPost> findByUserIdAndCreatedOnGreaterThanEqual(Long userId, Date startDate);
}
