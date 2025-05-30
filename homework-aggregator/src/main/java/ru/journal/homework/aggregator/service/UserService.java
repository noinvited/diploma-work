package ru.journal.homework.aggregator.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.journal.homework.aggregator.domain.*;
import ru.journal.homework.aggregator.domain.dto.UserEditDto;
import ru.journal.homework.aggregator.domain.dto.UserListDto;
import ru.journal.homework.aggregator.domain.dto.UserProfileDto;
import ru.journal.homework.aggregator.domain.helperEntity.Role;
import ru.journal.homework.aggregator.domain.helperEntity.Status;
import ru.journal.homework.aggregator.repo.*;
import ru.journal.homework.aggregator.utils.mapper.UserEditMapper;
import ru.journal.homework.aggregator.utils.mapper.UserListMapper;
import ru.journal.homework.aggregator.utils.mapper.UserProfileMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final GroupRepo groupRepo;
    private final DisciplineRepo disciplineRepo;
    private final TeacherRepo teacherRepo;
    private final PasswordEncoder passwordEncoder;
    private final TeacherDisciplineRepo teacherDisciplineRepo;
    private final TeacherGroupRepo teacherGroupRepo;
    private final GroupDisciplineRepo groupDisciplineRepo;

    private final MailSenderService mailSenderService;

    private final UserListMapper userListMapper;
    private final UserEditMapper userEditMapper;
    private final UserProfileMapper userProfileMapper;

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

        boolean isFioChanged = (fio != null) && !fio.isEmpty() && !fio.equals(userFio);

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

    public void userDelete(User user){
        userRepo.deleteById(user.getId());
    }

    @Transactional
    public int updateUser(User user, String username, Map<String, String> form,
                              String role, String statusUser, String studentId,
                              Long group, List<Long> disciplines, List<Long> teacherGroups) {
        User userFromDb = userRepo.findUserByUsername(username);
        boolean sendMessage = false;

        if(!userFromDb.getRole().equals(Role.valueOf(role))){
            sendMessage = true;
        }

        if(!userFromDb.getUsername().equals(username) && userRepo.existsByUsername(username)) {
            return 0;
        }
        user.setUsername(username);

        user.setActive(form.containsKey("status"));

        if(role.equals(Role.USER.toString())){
            user.setRole(Role.USER);

            if(statusUser != null){
                if(userFromDb.getStatus() == null){
                    sendMessage = true;
                }
                if(statusUser.equals(Status.STUDENT.toString())){
                    if(org.apache.commons.lang3.StringUtils.isBlank(studentId)){
                        return 2;
                    }
                    Long studentIdNumber = Long.parseLong(studentId);

                    // Проверяем существующего студента
                    Student student = studentRepo.findStudentByUserId(user.getId());
                    
                    // Проверяем уникальность студенческого билета
                    if(studentRepo.existsByStudentTicket(studentIdNumber)) {
                        // Если это не тот же самый студент
                        if(student == null || !student.getStudentTicket().equals(studentIdNumber)) {
                            return 1;
                        }
                    }

                    if(group == null){
                        return 3;
                    }

                    // создаем и обновляем студента
                    user.setStatus(Status.STUDENT);
                    if(student == null){
                        student = new Student();
                        student.setUser(user);
                    }
                    student.setStudentTicket(studentIdNumber);
                    student.setGroup(groupRepo.findById(group).get());
                    studentRepo.save(student);

                    // Удаляем информацию о преподавателе, если она есть
                    if (teacherRepo.existsByUserId(user.getId())) {
                        teacherRepo.delete(teacherRepo.findTeacherByUserId(user.getId()));
                    }
                } else {
                    // Проверяем существующего преподавателя
                    Teacher teacher = teacherRepo.findTeacherByUserId(user.getId());
                    if(teacher == null) {
                        teacher = new Teacher();
                        teacher.setUser(user);
                    }

                    // Проверяем наличие дисциплин
                    if(disciplines == null || disciplines.isEmpty()) {
                        return 4;
                    }

                    // Проверяем наличие групп
                    if(teacherGroups == null || teacherGroups.isEmpty()) {
                        return 5;
                    }

                    // Сохраняем преподавателя
                    user.setStatus(Status.TEACHER);
                    teacher = teacherRepo.save(teacher);

                    // Очищаем старые связи
                    teacherDisciplineRepo.deleteByTeacherId(teacher.getId());
                    teacherGroupRepo.deleteByTeacherId(teacher.getId());

                    // Добавляем дисциплины
                    for(Long disciplineId : disciplines) {
                        TeacherDiscipline teacherDiscipline = new TeacherDiscipline();
                        teacherDiscipline.setTeacher(teacher);
                        teacherDiscipline.setDiscipline(disciplineRepo.findById(disciplineId).get());
                        teacherDisciplineRepo.save(teacherDiscipline);
                    }

                    // Добавляем группы
                    for(Long groupId : teacherGroups) {
                        TeacherGroup teacherGroup = new TeacherGroup();
                        teacherGroup.setTeacher(teacher);
                        teacherGroup.setGroup(groupRepo.findById(groupId).get());
                        teacherGroupRepo.save(teacherGroup);
                    }

                    // Удаляем информацию о студенте, если она есть
                    if(studentRepo.existsByUserId(user.getId())){
                        studentRepo.delete(studentRepo.findStudentByUserId(user.getId()));
                    }
                }
            }
        } else {
            user.setRole(Role.ADMIN);
            user.setStatus(null);

            if(studentRepo.existsByUserId(user.getId())){
                studentRepo.delete(studentRepo.findStudentByUserId(user.getId()));
            }
            if(teacherRepo.existsByUserId(user.getId())){
                teacherRepo.delete(teacherRepo.findTeacherByUserId(user.getId()));
            }
        }

        userRepo.save(user);

        if(sendMessage){
            if(!StringUtils.isEmpty(user.getEmail())){
                String message = String.format(
                        "Hello, %s! \n" +
                                "The account has been filled in by the administrator, and now the entire functionality of the site is available to you.",
                        user.getUsername()
                );
                String subject = "Updated status by administrator";

                mailSenderService.send(user.getEmail(), subject, message);
            }
        }

        return 6;
    }

    public List<UserListDto> getAllUserListDto(){
        return userListMapper.toDtoList(findAll());
    }

    public UserEditDto getUserEditDto(User user){
        return userEditMapper.toDto(user);
    }

    public UserProfileDto getUserProfileDto(User user){
        return userProfileMapper.toDto(user);
    }

    public String getGroupNameByStudentUser(User user){
        if(user.getStatus() != null && user.getStatus() == Status.STUDENT) {
            Student student = studentRepo.findStudentByUserId(user.getId());
            if (student != null) {
                return student.getGroup().getNameGroup();
            }
        }
        return null;
    }

    public Long getGroupByStudentUser(User user){
        if(user.getStatus() != null && user.getStatus() == Status.STUDENT) {
            Student student = studentRepo.findStudentByUserId(user.getId());
            if (student != null) {
                return student.getGroup().getId();
            }
        }
        return null;
    }

    public List<Group> getGroupsNameByTeacherUser(User user){
        if(user.getStatus() != null && user.getStatus() == Status.TEACHER) {
            Teacher teacher = teacherRepo.findTeacherByUserId(user.getId());
            if (teacher != null) {
                return teacherGroupRepo.findAllGroupsByTeacherId(teacher.getId());
            }
        }
        return null;
    }

    public List<Discipline> getDisciplinesNameByTeacherUser(User user){
        if(user.getStatus() != null && user.getStatus() == Status.TEACHER) {
            Teacher teacher = teacherRepo.findTeacherByUserId(user.getId());
            if (teacher != null) {
                return teacherDisciplineRepo.findAllDisciplinesByTeacherId(teacher.getId());
            }
        }
        return null;
    }

    public Long getStudentTicketByUser(User user){
        if(user.getStatus() != null && user.getStatus() == Status.STUDENT) {
            Student student = studentRepo.findStudentByUserId(user.getId());
            if (student != null) {
                return student.getStudentTicket();
            }
        }
        return null;
    }

    public List<Group> getAllGroups(){
        return groupRepo.findAll();
    }

    public List<Discipline> getAllDiscipline(){
        return disciplineRepo.findAll();
    }

    public List<Group> getGroupsByDisciplines(List<Long> disciplineIds){
        return groupDisciplineRepo.findAllGroupsByDisciplineIds(disciplineIds);
    }

    private boolean emailValidation(String emailAddress) {
        return Pattern.compile("^(.+)@(\\S+)$")
                .matcher(emailAddress)
                .matches();
    }
}
