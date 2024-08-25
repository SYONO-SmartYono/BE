package paengbeom.syono.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CONNECTED_FINANCE")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConnectedFinance extends BaseEntiry{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONNECTED_FINANCE_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ACCOUNT_NUM")
    private String accountNum;

    @Column(name = "CARD_NUM")
    private String cardNum;

    @ManyToOne
    @JoinColumn(name = "CONNECTED_COMPANY_ID")
    ConnectedCompany connectedCompany;
}
