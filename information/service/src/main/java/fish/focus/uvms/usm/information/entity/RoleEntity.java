package fish.focus.uvms.usm.information.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@SequenceGenerator(name = "roleSequence", sequenceName = "SQ_ROLE", allocationSize = 1)
@Table(name = "ROLE_T")
@NamedQueries({
        @NamedQuery(name = "RoleEntity.findByRoleId", query = "SELECT r FROM RoleEntity r LEFT JOIN FETCH r.featureList WHERE r.roleId = :roleId"),
        @NamedQuery(name = "RoleEntity.findByName", query = "SELECT r FROM RoleEntity r WHERE r.name = :name")})
public class RoleEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleSequence")
    private Long roleId;

    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Basic(optional = false)
    @Column(name = "STATUS")
    private String status;

    @ManyToMany
    @JoinTable(name = "PERMISSION_T",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "FEATURE_ID", referencedColumnName = "FEATURE_ID")})
    private List<FeatureEntity> featureList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserContextEntity> userContextList;

    public RoleEntity() {
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FeatureEntity> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<FeatureEntity> featureList) {
        this.featureList = featureList;
    }

    public List<UserContextEntity> getUserContextList() {
        return userContextList;
    }

    public void setUserContextList(List<UserContextEntity> userContextList) {
        this.userContextList = userContextList;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "roleId=" + roleId +
                ", name=" + name +
                ", description=" + description +
                ", status=" + status +
                '}';
    }
}
