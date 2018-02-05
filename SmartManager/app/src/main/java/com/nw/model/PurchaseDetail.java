package com.nw.model;

public class PurchaseDetail
{
    private String AppraisalId;
    private String AccountNo;
    private String BoughtFrom;
    private String Comments;
    private String Date;
    private String Details;
    private String FinanceHouse;
    private String PurchaseDetailsId;
    private String SettlementR;

    public String getAppraisalId()
    {
        return AppraisalId;
    }

    public void setAppraisalId(String appraisalId)
    {
        if (appraisalId.trim().equals("anyType{}"))
        {
            AppraisalId = "";
        } else
        {
            AppraisalId = appraisalId;
        }
    }

    public String getAccountNo()
    {
        return AccountNo;
    }

    public void setAccountNo(String accountNo)
    {
        if (accountNo.trim().equals("anyType{}"))
        {
            AccountNo = "";
        } else
        {
            AccountNo = accountNo;
        }
    }

    public String getBoughtFrom()
    {
        return BoughtFrom;
    }

    public void setBoughtFrom(String boughtFrom)
    {
        if (boughtFrom.trim().equals("anyType{}"))
        {
            BoughtFrom = "";
        } else
        {
            BoughtFrom = boughtFrom;
        }
    }

    public String getComments()
    {
        return Comments;
    }

    public void setComments(String comments)
    {
        if (comments.trim().equals("anyType{}"))
        {
            Comments = "";
        } else
        {
            Comments = comments;
        }
    }

    public String getDate()
    {
        return Date;
    }

    public void setDate(String date)
    {
        if (date.trim().equals("anyType{}"))
        {
            Date = "";
        } else
        {
            Date = date;
        }
    }

    public String getDetails()
    {
        return Details;
    }

    public void setDetails(String details)
    {
        if (details.trim().equals("anyType{}"))
        {
            Details = "";
        } else
        {
            Details = details;
        }
    }

    public String getFinanceHouse()
    {
        return FinanceHouse;
    }

    public void setFinanceHouse(String financeHouse)
    {
        if (financeHouse.trim().equals("anyType{}"))
        {
            FinanceHouse = "";
        } else
        {
            FinanceHouse = financeHouse;
        }
    }

    public String getPurchaseDetailsId()
    {
        return PurchaseDetailsId;
    }

    public void setPurchaseDetailsId(String purchaseDetailsId)
    {
        if (purchaseDetailsId.trim().equals("anyType{}"))
        {
            PurchaseDetailsId = "";
        } else
        {
            PurchaseDetailsId = purchaseDetailsId;
        }
    }

    public String getSettlementR()
    {
        return SettlementR;
    }

    public void setSettlementR(String settlementR)
    {
        if (settlementR.trim().equals("anyType{}"))
        {
            SettlementR = "";
        } else
        {
            SettlementR = settlementR;
        }
    }
}
