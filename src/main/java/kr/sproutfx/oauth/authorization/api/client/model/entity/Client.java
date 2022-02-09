package kr.sproutfx.oauth.authorization.api.client.model.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import kr.sproutfx.oauth.authorization.api.client.model.enumeration.ClientStatus;
import kr.sproutfx.oauth.authorization.common.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id", callSuper = false)
@Entity @Table(name = "clients")
@DynamicInsert @DynamicUpdate @Audited
@SQLDelete(sql = "UPDATE clients SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Client extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "varchar(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String code;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String secret;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String accessTokenSecret;

    @Column(nullable = false, columnDefinition = "integer")
    private long accessTokenValidityInSeconds;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String refreshTokenSecret;

    @Column(nullable = false, columnDefinition = "integer")
    private long refreshTokenValidityInSeconds;
    
    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column(nullable = true, columnDefinition = "varchar(255)")
    private String description;
}