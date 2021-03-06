package fish.focus.uvms.usm.information.entity;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "preferenceSequence", sequenceName = "SQ_PREFERENCE", allocationSize = 1)
@Table(name = "PREFERENCE_T")
@NamedQueries({
        @NamedQuery(name = "PreferenceEntity.findByContextId", query = "SELECT p FROM PreferenceEntity p WHERE p.userContext.userContextId = :contextId")
})
public class PreferenceEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "PREFERENCE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preferenceSequence")
    private Long preferenceId;

    @Basic(optional = false)
    @Column(name = "OPTION_VALUE")
    private byte[] optionValue;

    @JoinColumn(name = "USER_CONTEXT_ID", referencedColumnName = "USER_CONTEXT_ID")
    @ManyToOne(optional = false)
    private UserContextEntity userContext;

    @JoinColumn(name = "OPTION_ID", referencedColumnName = "OPTION_ID")
    @ManyToOne(optional = false)
    private OptionEntity option;

    public PreferenceEntity() {
    }

    public Long getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Long preferenceId) {
        this.preferenceId = preferenceId;
    }

    public byte[] getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(byte[] optionValue) {
        this.optionValue = optionValue;
    }

    public UserContextEntity getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContextEntity userContext) {
        this.userContext = userContext;
    }

    public OptionEntity getOption() {
        return option;
    }

    public void setOption(OptionEntity option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "PreferenceEntity{" +
                "preferenceId=" + preferenceId +
                ", optionValue=" + optionValue +
                ", userContext=" + userContext +
                '}';
    }
}
