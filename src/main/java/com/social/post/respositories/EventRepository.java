package com.social.post.respositories;

import com.social.post.entities.EventPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface EventRepository extends MongoRepository<EventPost, String> {

    @Query("{ 'userId': ?0, 'createdOn': { $gte: ?1 } }")
    List<EventPost> findByUserIdAndCreatedOnGreaterThanEqual(long userId, Date startDate);
}
