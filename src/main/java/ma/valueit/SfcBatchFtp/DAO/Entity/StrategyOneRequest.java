package ma.valueit.SfcBatchFtp.DAO.Entity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "StrategyOneRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class StrategyOneRequest {

    @XmlElement(name = "Header")
    private Header header;

    @XmlElement(name = "Body")
    private InputEntity body;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        @XmlElement(name = "InquiryCode")
        private String inquiryCode = "REQUESTID";

        @XmlElement(name = "ProcessCode")
        private String processCode = "STRG_NOTATION";

        @XmlElement(name = "OrganizationCode")
        private String organizationCode = "ORGID";
    }
}