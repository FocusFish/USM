package fish.focus.uvms.usm.information.entity;

import javax.persistence.*;

@Entity
@Table(name = "CHANNEL_T")
@SequenceGenerator(name = "channelSequence", sequenceName = "SQ_CHANNEL", allocationSize = 1)
@NamedQueries({
        @NamedQuery(name = "ChannelEntity.findByChannelId", query = "SELECT c FROM ChannelEntity c WHERE c.channelId = :channelId"),
        @NamedQuery(name = "ChannelEntity.findByEndPointId", query = "SELECT c FROM ChannelEntity c WHERE c.endPoint.endPointId = :endPointId"),
        @NamedQuery(name = "ChannelEntity.findByDataFlowServiceEndPoint", query = "SELECT c FROM ChannelEntity c WHERE c.dataflow = :dataflow" +
                        " and c.service=:service and c.endPoint.endPointId=:endPointId")})
public class ChannelEntity extends AbstractAuditedEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channelSequence")
    @Column(name = "CHANNEL_ID")
    private Long channelId;

    @Basic(optional = false)
    @Column(name = "DATAFLOW")
    private String dataflow;

    @Basic(optional = false)
    @Column(name = "SERVICE")
    private String service;

    @Basic(optional = false)
    @Column(name = "PRIORITY")
    private Integer priority;

    @JoinColumn(name = "END_POINT_ID", referencedColumnName = "END_POINT_ID")
    @ManyToOne
    private EndPointEntity endPoint;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getDataflow() {
        return dataflow;
    }

    public void setDataflow(String dataflow) {
        this.dataflow = dataflow;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public EndPointEntity getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPointEntity endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return "ChannelEntity [channelId=" + channelId + ", dataflow="
                + dataflow + ", service=" + service + ", priority=" + priority
                + ", endPoint=" + endPoint + "]";
    }

}
