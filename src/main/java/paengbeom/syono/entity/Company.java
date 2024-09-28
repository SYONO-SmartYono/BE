package paengbeom.syono.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "COMPANY")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Company extends BaseEntiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "TYPE", nullable = false)
    private String type;

}
