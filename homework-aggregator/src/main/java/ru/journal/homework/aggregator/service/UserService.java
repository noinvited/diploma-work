package ru.journal.homework.aggregator.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.journal.homework.aggregator.domain.Student;
import ru.journal.homework.aggregator.domain.User;
import ru.journal.homework.aggregator.domain.dto.UserListDto;
import ru.journal.homework.aggregator.domain.helperEntity.Role;
import ru.journal.homework.aggregator.repo.StudentRepo;
import ru.journal.homework.aggregator.repo.UserRepo;
import ru.journal.homework.aggregator.utils.mapper.UserListMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    private final UserListMapper userListMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public boolean addUser(User user){
        User userFromDb = userRepo.findUserByUsername(user.getUsername());

        if(userFromDb != null){
            return false;
        }

        user.setActive(false);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to HW aggregator, Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode()
            );
            String subject = "Activation code";

            mailSenderService.send(user.getEmail(), subject, message);
        }

        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findUserByActivationCode(code);

        if(user == null){
            return false;
        }

        user.setActive(true);
        user.setActivationCode(null);

        userRepo.save(user);
        return true;
    }

    public int updateEmail(User user, String email){
        String userEmail = user.getEmail();

        boolean isEmailChanged = !email.equals(userEmail);

        if(isEmailChanged){
            if(emailValidation(email)){
                user.setEmail(email);
                userRepo.save(user);

                String message = String.format("Hello, %s! \n" +
                                "Your email has been changed to this one!\nSincerely, the HW-aggregator team.",
                        user.getUsername());

                String subject = "Update email";
                mailSenderService.send(user.getEmail(), subject, message);
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public int updatePassword(User user, String oldPassword, String newPassword){
        if(!StringUtils.isEmpty(newPassword) && !StringUtils.isEmpty(oldPassword)){
            if(passwordEncoder.matches(oldPassword, user.getPassword())){
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public int updateFio(User user, String fio){
        String userFio = user.getFio();

        boolean isFioChanged = (fio != null) && !fio.equals(userFio);

        if(isFioChanged){
            user.setFio(fio);
            userRepo.save(user);
            return 1;
        }
        return 0;
    }

    public int updateBirthdate(User user, LocalDate birthdate){
        if (birthdate == null) {
            return 0;
        }

        LocalDate userBirthdate = user.getBirthdate();

        boolean isBirthdateChanged = (userBirthdate == null) || !birthdate.isEqual(userBirthdate);

        if (isBirthdateChanged) {
            if (birthdate.isBefore(LocalDate.now())){
                user.setBirthdate(birthdate);
                userRepo.save(user);
                return 2;
            }
            return 1;
        }

        return 0;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public List<UserListDto> getAllUserListDto(){
        return userListMapper.toDtoList(findAll());
    }

    public String getGroupNameByUser(User user){
        if(user.getStatus() != null) {
            Student student = studentRepo.findStudentByUserId(user.getId());
            return student.getGroup().getNameGroup();
        }
        return null;
    }

    private boolean emailValidation(String emailAddress) {
        return Pattern.compile("^(.+)@(\\S+)$")
                .matcher(emailAddress)
                .matches();
    }
}
