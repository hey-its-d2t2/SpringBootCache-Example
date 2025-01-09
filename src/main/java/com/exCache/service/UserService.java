package com.exCache.service;

import com.exCache.entity.User;
import com.exCache.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*1. Basic Caching using @Cacheable

        The @Cacheable annotation caches the result of a method based on its input arguments.
        When the method is called with the same arguments again,
        it retrieves the result from the cache instead of executing the method.

        - Value: This specifies which cache to use.
        - Key: Defines the key for caching. Here, the user ID is used as the key.
        */

    @Cacheable(value = "userCacheData", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    /*2. Caching Collections with @Cacheable

         By caching this method, you avoid hitting the database each time you need the list of all users.*/

    @Cacheable(value = "userCacheData")
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /*3. Refreshing Cache with @CachePut

        The @CachePut annotation is used when you want to update the cache alongside saving data.
        It does not check for existing cache; it simply updates or adds a new entry.

        Explanation:
        Whenever a new user is created, the cache gets updated with the new user information.
        */

    @CachePut(value = "userCacheData", key = "#user.id")
    public User createUser(User user) {
        return userRepository.save(user);
    }


    /*4. Deleting Cache Entries with @CacheEvict
        When data is modified (like updating or deleting a user), you should also manage the cache properly.
        The @CacheEvict annotation removes entries from the cache.

        Explanation:
        When a user is updated or deleted, the corresponding entry in the cache is removed,
        ensuring that stale data does not persist.
        */

    @CacheEvict(value = "userCacheData", key="#id")
    public User updateUser(Long id,User user) {
        //Finding the existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));

        //Updating all with incoming new data from the incoming user object (excluding ID)
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setGender(user.getGender());
        existingUser.setEmail(user.getEmail());

        //Save the updated user
        return userRepository.save(existingUser);

        /*After all the required fields are updated/deleted, the existingUser is saved back to the database,
        which keeps the cache in sync due to the @CacheEvict annotation that invalidates the cached entry.*/
    }

    @CacheEvict(value = "userCacheData", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /*5. Caching Results for Multiple Methods
        If you have scenarios where multiple methods use the same caching logic,
        you can combine caching on those as well:

        @Cacheable(value = "usersCache", key = "#id")
        public User getUserById(Long id) {
            return userRepository.findById(id).orElse(null);
        }

        @CacheEvict(value = "usersCache", key = "#user.id")
        public User updateUser(Long id, User user) {
            user.setId(id);
            return userRepository.save(user);
        }

        @CacheEvict(value = "usersCache", key = "#id")
        public void deleteUser(Long id) {
            userRepository.deleteById(id);
        }

       */


    /*6. Cache Control with @Cacheable Condition and Unless
        Sometimes, you may want to cache conditionally based on certain conditions:

        Explanation:
        Condition: Only cache if the condition is met (user age is greater than or equal 18).
        Unless: Do not cache if the result is null
        */

    @Cacheable(value = "userCacheData", key ="#age", condition = "#age >= 18", unless = "#result ==null")
    public Object getUserByAge(int age) {
        return userRepository.findByAge(age).orElseThrow(()-> new RuntimeException("No Data"));
    }


    /*Summary of Caching Techniques
        - Basic Caching: Using @Cacheable to store method results for fast retrieval.
        - Caching Collections: Cache results from list returning methods to reduce database access.
        - Cache Update: Use @CachePut to ensure that after creation or updates, the cache has the latest data.
        - Cache Eviction: @CacheEvict effectively removes stale data from the cache when updates or deletes happen.
        - Conditional Caching: Implements logic to decide whether to cache results based on input parameters and output values.

    *Best Practices for Caching
        - Choose the Right Caching Strategy: Based on usage patterns, pick the right method (e.g., caching frequently accessed data, avoiding cache for less critical data).
        - Monitor Cache Usage: Keep an eye on cache hit rates to adjust strategies.
        - Set Appropriate Lifetimes: Define reasonable expiry times for your cache entries based on data volatility.
        - Optimize Size: Control how much data is being cached to avoid memory overflow.*/

}
