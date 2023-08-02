const express = require('express');
const app = express();
const axios = require('axios')
const path = require('path');
const cors = require('cors');
const { createCipher } = require('crypto');
const { response } = require('express');
const { networkInterfaces } = require('os');

//app.use (express.static(path.join( __dirname,'../my-app/dist/my-app')));
app.use(cors({
    origin: '*'
}));

app.get('/', (req, res) => {
  res.send('Hello from App Engine!');
  //res.sendFile(path.join(__dirname, '../client/src/', 'index.html'));
});

app.get('/company-description/:symbol', (req, res) => {
  let url = `https://finnhub.io/api/v1/stock/profile2?symbol=${req.params.symbol}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
          res.send(response.data);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})


app.get('/company-history/:symbol/:resolution/:from/:to/:ctype', (req, res) => {
  if(req.params.symbol=='first'){
    var from_d=new Date(req.params.from)
    var to_d=new Date(req.params.to)
    var from_ts=Math.round(from_d.getTime() / 1000);
    var to_ts=Math.round(to_d.getTime() / 1000);
    var t_=[]
    var ct=[]
    let url = `https://finnhub.io/api/v1/stock/candle?symbol=${req.params.symbol.toUpperCase()}&resolution=${req.params.resolution}&from=${from_ts}&to=${to_ts}&token=c7pktlqad3iamlesaspg`
    axios.get(url) // API url which is getting data
        .then((response) => {
            response.data.t.map((ele)=>{
              t_.push(ele*1000)
            })
            ct = t_.map(function(e, i) {
              return [e,response.data.c[i]];
            });
            var finalData={
              c:response.data.c,
              h:response.data.h,
              l:response.data.l,
              o:response.data.o,
              s:response.data.s,
              v:response.data.v,
              t:response.data.t,
              ct:ct
            }
            res.send(finalData);
        })
        .catch((error) => {
            console.log(error);
            res.send(error)
        });
  }
  else{
    var from_d=new Date(req.params.from)
    var to_d=new Date(req.params.to)
    var from_ts=Math.round(from_d.getTime() / 1000);
    var to_ts=Math.round(to_d.getTime() / 1000);
    let url = `https://finnhub.io/api/v1/stock/candle?symbol=${req.params.symbol.toUpperCase()}&resolution=${req.params.resolution}&from=${from_ts}&to=${to_ts}&token=c7pktlqad3iamlesaspg`
    axios.get(url) // API url which is getting data
    .then((response) => {
        var t= response.data.t.map((ele)=>{
          return(ele*1000)
        })
        var final={
          c:response.data.c,
          h:response.data.h,
          l:response.data.l,
          o:response.data.o,
          s:response.data.s,
          v:response.data.v,
          t:t,
        }
        res.send(final);
    })
    .catch((error) => {
        console.log(error);
        res.send(error)
    });
  }

})

app.get('/company-stock-price/:symbol', (req, res) => {
  let url = `https://finnhub.io/api/v1/quote?symbol=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
          res.send(response.data);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})

app.get('/autocomplete/:symbol', (req, res) => {
  let url = `https://finnhub.io/api/v1/search?q=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
          res.send(response.data);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})

function mydate(date)
{
  var month = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  return month[date.getMonth()]+' '+date.getDate()+', '+date.getFullYear()
}

//change to 7 days
app.get('/company-news/:symbol', (req, res) => {
  var todayDate = new Date().toISOString().slice(0, 10);
  var before7 = new Date()
  before7.setDate(before7.getDate()-7)
  var b47 = before7.toISOString().slice(0, 10);
  var finaldata=[]
  let url = `https://finnhub.io/api/v1/company-news?symbol=${req.params.symbol.toUpperCase()}&from=${b47}&to=${todayDate}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
          var count=0
          response.data.map((ele)=>{
              if(ele.datetime && ele.headline && ele.image && ele.source && ele.summary && ele.url && count<20){
                count+=1
                var pudDate=mydate(new Date(ele.datetime*1000))
                var newData={
                    datetime:pudDate,
                    headline:ele.headline,
                    image:ele.image,
                    source:ele.source,
                    summary:ele.summary,
                    url:ele.url,
                }
                finaldata.push(newData)
              }
          })
          res.send(finaldata);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})


app.get('/company-recommendation/:symbol', (req, res) => {
  var buy=[]
  var sell=[]
  var hold=[]
  var period=[]
  var strongBuy=[]
  var strongSell=[]
  var symbol=[]
  let url = `https://finnhub.io/api/v1/stock/recommendation?symbol=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
        response.data.map((ele)=>{
          buy.push(ele.buy)
          sell.push(ele.sell)
          hold.push(ele.hold)
          period.push(ele.period)
          strongBuy.push(ele.strongBuy)
          strongSell.push(ele.strongSell)
          symbol.push(ele.symbol)
        })
        var finalData={
          buy:buy,
          sell:sell,
          hold:hold,
          period:period,
          strongBuy:strongBuy,
          strongSell:strongSell,
          symbol:symbol
        }
        res.send(finalData);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})

app.get('/company-social-sentiment/:symbol', (req, res) => {
  var finalData={}
  let url = `https://finnhub.io/api/v1/stock/social-sentiment?symbol=${req.params.symbol.toUpperCase()}&from=2022-01-01&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
        var redtot=0,twitot=0,redpos=0,twipos=0,redneg=0,twineg=0;
        response.data.reddit.map((red)=>{
          redtot+=red.mention;
          redneg+=red.negativeMention;
          redpos+=red.positiveMention;
        })
        response.data.twitter.map((twi)=>{
          twitot+=twi.mention;
          twineg+=twi.negativeMention;
          twipos+=twi.positiveMention;
        })
        finalData={
            symbol:response.data.symbol,
            redtot:redtot,
            redneg:redneg,
            redpos:redpos,
            twitot:twitot,
            twineg:twineg,
            twipos:twipos
        }
        res.send(finalData);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})

app.get('/company-peers/:symbol', (req, res) => {
  let url = `https://finnhub.io/api/v1/stock/peers?symbol=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
        res.send(response.data);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})

app.get('/company-earnings/:symbol', (req, res) => {
  var finalData={}
  var actual=[]
  var estimate=[];
  var surprise=[];
  var period=[];
  var surprisePercent=[];
  var symbol=[];
  var categories=[]
  let url = `https://finnhub.io/api/v1/stock/earnings?symbol=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`
  axios.get(url) // API url which is getting data
      .then((response) => {
        response.data.map((ele)=>{
          actual.push(ele.actual?ele.actual:0)
          estimate.push(ele.estimate?ele.estimate:0)
          surprise.push(ele.surprise?ele.surprise:0)
          period.push(ele.period)
          surprisePercent.push(ele.surprisePercent?ele.surprisePercent:0)
          categories.push(ele.surprise && ele.period?ele.period+"<br>Surprise:"+ele.surprise:'')
          symbol.push(ele.symbol)
        })
                  finalData={
            actual:actual,
            estimate:estimate,
            surprise:surprise,
            period:period,
            surprisePercent:surprisePercent,
            symbol:symbol,
            categories:categories
          }
          res.send(finalData);
      })
      .catch((error) => {
          console.log(error);
          res.send(error)
      });
})

app.get('/home/:data',(req,res)=>{
  var datasplit=req.params.data.split(";");
  var portarr=datasplit[0].split(",");
  var favarr=datasplit[1].split(",");
  var portfolio=[]
  var favorite=[]
  var p_res=[]
  var f_res=[]
  var finalData=[]
  portarr.map((ele)=>{
    portfolio.push(axios.get(`https://finnhub.io/api/v1/quote?symbol=${ele.toUpperCase()}&token=c7pktlqad3iamlesaspg`))
  })
  favarr.map((e)=>{
    favorite.push(axios.get(`https://finnhub.io/api/v1/quote?symbol=${e.toUpperCase()}&token=c7pktlqad3iamlesaspg`))
  })

  axios.all(portfolio).then(axios.spread((...responses) => {
    responses.map((e)=>{
      if(e.data && e.data.d!=null){
        p_res.push(e.data);
      }
    })
    finalData.push(p_res);
    axios.all(favorite).then(axios.spread((...responses) => {
      responses.map((e)=>{
        if(e.data && e.data.d!=null){
          f_res.push(e.data);
        }
      })
      finalData.push(f_res);
      res.send(finalData);
    })).catch(errors => {
      res.send(errors)
    })
  })).catch(errors => {
    res.send(errors)
  })
})

app.get('/alldata/:symbol', (req, res) => {
  var todayDate = new Date().toISOString().slice(0, 10);
  var before7 = new Date()
  before7.setDate(before7.getDate()-7)
  var b47 = before7.toISOString().slice(0, 10);
  var finaldata=[]
  var newsData=[]
  company=axios.get( `https://finnhub.io/api/v1/stock/profile2?symbol=${req.params.symbol}&token=c7pktlqad3iamlesaspg`);
  stock=axios.get(`https://finnhub.io/api/v1/quote?symbol=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`);
  news=axios.get(`https://finnhub.io/api/v1/company-news?symbol=${req.params.symbol.toUpperCase()}&from=${b47}&to=${todayDate}&token=c7pktlqad3iamlesaspg`);
  social=axios.get(`https://finnhub.io/api/v1/stock/social-sentiment?symbol=${req.params.symbol.toUpperCase()}&from=2022-01-01&token=c7pktlqad3iamlesaspg`);
  peers=axios.get(`https://finnhub.io/api/v1/stock/peers?symbol=${req.params.symbol.toUpperCase()}&token=c7pktlqad3iamlesaspg`);

  axios.all([company,stock,news,social,peers]).then(axios.spread((...responses) => {
    const responseOne = responses[0]
    const responseTwo = responses[1]
    const responesThree = responses[2]
    const responesFour = responses[3]
    const responesFive= responses[4]
    finaldata.push(responseOne.data);
    finaldata.push(responseTwo.data);
    if(responesThree.data){
      var count=0
      responesThree.data.map((ele)=>{
          if(ele.datetime && ele.headline && ele.image && ele.source && ele.summary && ele.url && count<20){
            count+=1
            var date1=new Date();
            var date2=new Date(ele.datetime*1000)
            var diff = (date1 - date2) / 3600000;
            var pudDate=mydate(new Date(ele.datetime*1000))
            var newData={
                datetime:pudDate,
                headline:ele.headline,
                image:ele.image,
                source:ele.source,
                summary:ele.summary,
                url:ele.url,
                diff:Math.floor(diff)
            }
            if(count==1){
              finaldata.push(newData);
            }
            else{
              newsData.push(newData)
            }
          }
    })
    finaldata.push(newsData)
  }
  if(responesFour.data){
    var redtot=0,twitot=0,redpos=0,twipos=0,redneg=0,twineg=0;
    responesFour.data.reddit.map((red)=>{
      redtot+=red.mention;
      redneg+=red.negativeMention;
      redpos+=red.positiveMention;
    })
    responesFour.data.twitter.map((twi)=>{
      twitot+=twi.mention;
      twineg+=twi.negativeMention;
      twipos+=twi.positiveMention;
    })
    var newData={
        symbol:responesFour.datasymbol,
        redtot:redtot,
        redneg:redneg,
        redpos:redpos,
        twitot:twitot,
        twineg:twineg,
        twipos:twipos
    }
    finaldata.push(newData);
  }
    finaldata.push(responesFive.data);
    res.send(finaldata);
  })).catch(errors => {
    // react on errors.
    res.send(errors);
  })
})

// Listen to the App Engine-specified port, or 8080 otherwise
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}...`);
});