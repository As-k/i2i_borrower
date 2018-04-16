package in.co.cioc.i2i;


public class Backend {
    //String BASE_URL = "http://10.0.2.2:8080";
    String BASE_URL = "http://qai2i.com:8080/";
//    String BASE_URL = "http://192.168.1.113:8080/";
//    String BASE_URL = "https://api.i2ifunding.com/";
    //String BASE_URL = "http://i2ifunding.cioc.co.in:8080";


    Backend(){

    }

}


///api/v1/borrowerRegistration/submitBasic

//mobileOTP	802902
//emailOTP	379096
//emailID	mobile@gmail.com

// /api/v1/notifications/
//userStatus	0
//reg_type	borrower
//reg_stage	1

/*http://localhost:8080/api/v1/checkEligibility/?csrf_token=XzaphWgrzwWRKtBjLkRneKYaq&session_id=gs7Ix9YWiMtmdNsbeFBYYhJIi
{
        "loanDetails": {
            "desiredAmount": 45000,
            "tenure": "6",
            "purpose": "4",
            "description": "fdfsdfsd"
        },
        "personalDetails": {
            "married": false,
            "dateOfBirth": "2018-01-11T04:54:00.000Z",
            "pincode": "201301",
            "city": "Gautam Buddha Nagar",
            "state": "UTTAR PRADESH\r"
        },
        "empType": "Self Employed",
        "employementDetails": {
            "type": "34234",
            "grossTurnover": 42342,
            "grossAnnualProfit": 423
        },
        "financialDetails": {
            "houseType": "Own",
            "otherMonthlyIncome": 323,
            "loanRunning": false,
            "runningLoanEmi": 0,
            "creditCard": false,
            "creditCardOutstanding": 0,
            "cibilScore": 23,
            "spouseMonthlyIncome": 0,
            "canProvideGurantor": null,
            "monthlyRent": 0,
            "stayingSince": null
            }
        }

        */