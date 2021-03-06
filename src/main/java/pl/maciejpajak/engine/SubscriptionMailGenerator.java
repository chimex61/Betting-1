package pl.maciejpajak.engine;

import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.maciejpajak.repository.CompetitionNewsRepository;
import pl.maciejpajak.repository.UserRepository;

/**
 * Class handles sending emails with subscriptions.
 * 
 * @author mac
 *
 */
@Component
public class SubscriptionMailGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(SubscriptionMailGenerator.class);
    
//    @Autowired
//    private JavaMailSender javaMailSender;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CompetitionNewsRepository competitionNewsRepository;
    
    /**
     * Creates email messages with subscriptions and sends them to users,
     * every day at midnight.
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void sendSubscriptions() {
        userRepository.findAllByVisible(true).forEach(u -> {
            StringBuilder sb = new StringBuilder();
            competitionNewsRepository
                .findAllBySentAndCompetitionCompetitionPreferencesUserIdAndCompetitionCompetitionPreferencesSubscribed(false, u.getId(), true)
                .stream().forEach(n -> 
                    sb.append(n.getContent()).append("\n")
                );
            if (sb.length() != 0) {
                sendSimpleMessage(u.getEmail(), "Subscription", sb.toString());
            }
        });
        competitionNewsRepository.save(competitionNewsRepository
                            .findAllBySent(false).stream().peek(n -> n.setSent(true)).collect(Collectors.toList()));
    }
    
    /**
     * Sends email {@code to} with {@code subject} and content {@code text}
     * @param to - recipient email
     * @param subject - message subject
     * @param text - message content
     */
    private void sendSimpleMessage (
            String to, String subject, String text) {
        log.debug("sending new message TO: {}\nSUBJECT: {}\nCONTENT: {}", to, subject, text);
        
        // for demonstration purposes email sending is only mocked
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        javaMailSender.send(message);
    }

}
