package fish.focus.uvms.usm.information.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@SequenceGenerator(name = "passwordHistSequence", sequenceName = "SQ_PASSWORD_HIST", allocationSize = 1)
@Table(name = "PASSWORD_HIST_T")
@NamedQueries({
        @NamedQuery(name = "PasswordHistEntity.findByPasswordHistId", query = "SELECT p FROM PasswordHistEntity p WHERE p.passwordHistId = :passwordHistId"),
        @NamedQuery(name = "PasswordHistEntity.findByUserName", query = "SELECT p FROM PasswordHistEntity p WHERE p.user.userName = :userName ORDER BY p.changedOn DESC")})
public class PasswordHistEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "PASSWORD_HIST_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordHistSequence")
    private Long passwordHistId;

    @Basic(optional = false)
    @Column(name = "PASSWORD")
    private String password;

    @Basic(optional = false)
    @Column(name = "CHANGED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changedOn;

    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne(optional = false)
    private UserEntity user;

    public PasswordHistEntity() {
    }

    public Long getPasswordHistId() {
        return passwordHistId;
    }

    public void setPasswordHistId(Long passwordHistId) {
        this.passwordHistId = passwordHistId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PasswordHistEntity{" +
                "passwordHistId=" + passwordHistId +
                ", changedOn=" + changedOn +
                '}';
    }
}
