package com.generated.fashionecommercewebsite.user;

import com.generated.fashionecommercewebsite.user.entity.UserAccount;
import com.generated.fashionecommercewebsite.user.repository.UserAccountRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public void syncUser(String genericUserId, String email, String firstName, String lastName, String phone) {
        userAccountRepository.findByGenericUserId(genericUserId)
                .ifPresentOrElse(
                        existing -> updateExisting(existing, email, firstName, lastName, phone),
                        () -> createNew(genericUserId, email, firstName, lastName, phone));
    }

    private void updateExisting(
            UserAccount existing,
            String email,
            String firstName,
            String lastName,
            String phone) {
        existing.setEmail(email);
        existing.setFirstName(firstName);
        if (lastName != null) {
            existing.setLastName(lastName);
        }
        if (phone != null) {
            existing.setPhone(phone);
        }
        existing.setUpdatedAt(Instant.now());
        userAccountRepository.save(existing);
    }

    private void createNew(String genericUserId, String email, String firstName, String lastName, String phone) {
        UserAccount account = new UserAccount();
        account.setGenericUserId(genericUserId);
        account.setEmail(email);
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPhone(phone);
        userAccountRepository.save(account);
    }
}
