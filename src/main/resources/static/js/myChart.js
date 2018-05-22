/**
 *
 * @param chartOption
 * @param data
 */
function drawChart(chartOption, data) {

    Highcharts.chart(chartOption.id, {
        chart: {
            type: 'spline'
        },
        title: {
            text: chartOption.title
        },
        xAxis: {
            title: {
                text: chartOption.xAxis
            },
            labels: {
                formatter: function () {
                    return this.value;
                }
            }
        },
        yAxis: {
            title: {
                text: chartOption.yAxis
            },
            labels: {
                formatter: function () {
                    return this.value;
                }
            }
        },
        plotOptions: {
            spline: {
                marker: {
                    enabled: false
                }
            }
        },
        tooltip: {
            formatter: function () {
                // return this.x + ' : ' + this.y;
                return chartOption.xAxis + ' : ' + this.x + '      ' + chartOption.yAxis + ' : ' + this.y;
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: true
        },
        series: [{
            data: data
        }]
    });
}


/**
 * 初始化与时间有关的chart
 * @param chartOption
 * @returns {Chart}
 */
function initializeChart(chartOption) {
    Highcharts.setOptions({
        global: {
            useUTC: true
        }
    });

    var options = {
        chart: {
            renderTo: chartOption.id,
            type: 'spline'
        },
        title: {
            text: chartOption.title
        },
        xAxis: {
            title: {
                text: chartOption.xAxis
            },
            type: 'datetime',
            // Sets tickInterval to 24 * 3600 * 1000 if display is by day
            // tickInterval:  2 * 3600 * 1000,
            dateTimeLabelFormats: {
                day: '%H:%M'
            }
        },
        yAxis: {
            title: {
                text: chartOption.yAxis
            }
        },
        plotOptions: {
            spline: {
                marker: {
                    enabled: false
                }
            }
        },
        tooltip: {
            formatter: function () {
                return Highcharts.dateFormat('%H:%M', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: true
        }
    };

    return new Highcharts.Chart(options);

}

/**
 * 在initializeChart基础上去掉了时间的格式化
 * @param chartOption<script th:src="@{/js/application.js}" src="../../static/js/application.js"></script>
 * @returns {Chart}
 */

function initializeCharts(chartOption) {
    Highcharts.setOptions({});

    var options = {
        chart: {
            renderTo: chartOption.id,
            type: 'spline'
        },
        title: {
            text: chartOption.title
        },
        xAxis: {
            title: {
                text: chartOption.xAxis
            },

        },
        yAxis: {
            title: {
                text: chartOption.yAxis
            }
        },
        plotOptions: {
            spline: {
                marker: {
                    enabled: false
                }
            }
        },
        tooltip: {
            formatter: function () {
                return chartOption.xAxis + ' : ' + this.x + '      ' + chartOption.yAxis + ' : ' + this.y;
            }
        },
        legend: {
            enabled: true
        },
        exporting: {
            enabled: true
        }
    };

    return new Highcharts.Chart(options);

}

// /**
//  * 为chart赋值，或者改变其中的series
//  * @param chart
//  * @param xData 横坐标值，一维数组
//  * @param yData 纵坐标值，一维或二维数组，二维中每一行代表一个系列
//  */
// function changeChartData(chart, date, yData) {
//
//     var xData = getTimeArray(date, 24);
//
//     console.log(xData);
//
//     var series = [];
//
//     // 多系列的数据
//     if (yData[0].constructor == Array) {
//         _.forEach(yData, function(value) {
//             series.push(_.zip(xData, value));
//         });
//     } else {
//         series.push(_.zip(xData, yData));
//     }
//
//     _.forEach(series, function (value) {
//         chart.addSeries({
//             data: value
//         });
//     });
// }

/**
 * 为chart赋值，或者改变其中的series
 * @param chart
 * @param xData 横坐标值，一维数组
 * @param yData 纵坐标值，一维或二维数组，二维中每一行代表一个系列
 */
function insertChartData(chart, xData, yData) {

    var series = [];

    // 多系列的数据
    if (yData[0].constructor == Array) {
        _.forEach(yData, function (value) {
            series.push(_.zip(xData, value));
        });
    } else {
        series.push(_.zip(xData, yData));
    }
    _.forEach(series, function (value) {

        chart.addSeries({
            data: value
        });
    });
}

/**
 * 为NQHchart赋值，或者改变其中的series
 * @param chart
 * @param xData 横坐标值，二维数组
 * @param yData 纵坐标值，二维数组
 * @param z 系列值
 */
function insertNQHChartData(chart, xData, yData, z) {

    var series = [];

    // 多系列的数据
    _.forEach(yData, function (value, index) {
        series.push(_.zip(xData[index], value));
    });

    _.forEach(series, function (value, index) {

        chart.addSeries({
            name: "H(m)：" + z[index],
            data: value

        });
    });
}


/**
 * 为chart赋值，或者改变其中的series
 * @param chart
 * @param xData 横坐标值，一维数组
 * @param yData 纵坐标值，一维或二维数组，二维中每一行代表一个系列
 */
function changeChartData(chart, xData, yData) {

    var series = [];

    // 多系列的数据
    if (yData[0].constructor == Array) {
        _.forEach(yData, function (value) {
            series.push(_.zip(xData, value));
        });
    } else {
        series.push(_.zip(xData, yData));
    }

    _.forEach(series, function (value, index) {
        chart.series[index].setData(value);
    });
}


function getTimeArray(date, number) {
    var dateCopy = new Date(date.getTime());
    var timeArray = [];
    var increament = 3600 * 24 * 1000 / number;

    for (var i = 0; i < number; i++) {
        timeArray.push(dateCopy.getTime());
        dateCopy.setTime(timeArray[i] + increament);
    }

    return timeArray;
}


function formatDate(date) {
    var hours = date.getUTCHours();
    hours = hours < 10 ? '0' + hours : hours;
    var minutes = date.getUTCMinutes();
    minutes = minutes < 10 ? '0' + minutes : minutes;
    return hours + ':' + minutes;
}

/**
 * 数组保留几位小数
 * @param array
 * @param number
 * @returns {Array}
 */
function arraytoFixed(array, number) {
    var result = [];
    var pow = Math.pow(10, number);
    if (array[0].constructor == Array) {
        _.forEach(array, function (value, index) {
            result[index] = [];
            _.forEach(value, function (a) {
                result[index].push(Math.round(a * pow) / pow);
            })
        });
    } else {
        _.forEach(array, function (value) {
            result.push(Math.round(value * pow) / pow);
        })
    }

    return result;
}



