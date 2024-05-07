// USUARIOS
function cargarGraficoUsuarios(datosUsuariosPorRol) {
    const roles = Object.keys(datosUsuariosPorRol);
    const cantidadUsuarios = Object.values(datosUsuariosPorRol);

    const ctx1 = document.getElementById('chartUsuariosPorRol').getContext('2d');

    new Chart(ctx1, {
       type: 'bar',
       data: {
           labels: roles,
           datasets: [{
               data: cantidadUsuarios,
               backgroundColor: [
                   'rgba(255, 99, 132, 0.5)',
                   'rgba(54, 162, 235, 0.5)',
                   'rgba(255, 206, 86, 0.5)'
               ],
               borderColor: [
                   'rgba(255, 99, 132, 1)',
                   'rgba(54, 162, 235, 1)',
                   'rgba(255, 206, 86, 1)'
               ],
               borderWidth: 1,
               label: ''
           }]
       },
       options: {
          plugins: {
            legend: {
              display: false,
            },
          },
          scales: {
            y: {
                ticks: {
                    precision: 0
                }
            }
          }
       }
    });
}

function cargarGraficoCategorias(datosProductosPorCategoria) {

    const categorias = Object.keys(datosProductosPorCategoria);
    const cantidadProductos = Object.values(datosProductosPorCategoria);

    const ctx3 = document.getElementById('chartProductosPorCategoria').getContext('2d');

    new Chart(ctx3, {
       type: 'bar',
       data: {
           labels: categorias,
           datasets: [{
               data: cantidadProductos,
               backgroundColor: 'rgba(54, 162, 235, 0.5)',
               borderColor: 'rgba(54, 162, 235, 1)',
               borderWidth: 1
           }]
       },
       options: {
           plugins: {
               legend: {
                  display: false,
               },
           },
           scales: {
               yAxes: [{
                   ticks: {
                       beginAtZero: true
                   }
               }]
           }
       }
    });
}

function cargarGraficoPedidosEstado(datosEstadoPedidos) {

    const estadosPedidos = Object.keys(datosEstadoPedidos);
    const cantidadPedidos = Object.values(datosEstadoPedidos);

    const ctx2 = document.getElementById('chartEstadoPedidos').getContext('2d');

     const coloresBarras = [
            'rgba(255, 99, 132, 0.5)',
            'rgba(54, 162, 235, 0.5)',
            'rgba(255, 206, 86, 0.5)',
            'rgba(75, 192, 192, 0.5)',
            'rgba(153, 102, 255, 0.5)',
            'rgba(255, 159, 64, 0.5)'
     ];

    new Chart(ctx2, {
       type: 'bar',
       data: {
           labels: estadosPedidos,
           datasets: [{
               data: cantidadPedidos,
               backgroundColor: coloresBarras,
               borderColor: coloresBarras.map(color => color.replace('0.5', '1')),
               borderWidth: 1
           }]
       },
       options: {
           plugins: {
                  legend: {
                     display: false,
                  },
           },
           scales: {
               yAxes: [{
                   ticks: {
                       beginAtZero: true
                   }
               }]
           }
       }
    });
}

function cargarGraficoVentasAnuales(datosVentasAnuales) {
    const año = Object.keys(datosVentasAnuales);
    const ventas = Object.values(datosVentasAnuales);

    const ctxVA = document.getElementById('chartVentasAnuales').getContext('2d');

    const coloresBarras = [
        'rgba(255, 99, 132, 0.5)',
        'rgba(54, 162, 235, 0.5)',
        'rgba(255, 206, 86, 0.5)',
        'rgba(75, 192, 192, 0.5)',
        'rgba(153, 102, 255, 0.5)',
        'rgba(255, 159, 64, 0.5)'
    ];

    new Chart(ctxVA, {
        type: 'bar',
        data: {
            labels: año,
            datasets: [{
                data: ventas,
                backgroundColor: coloresBarras,
                borderColor: coloresBarras.map(color => color.replace('0.5', '1')),
                borderWidth: 1
            }]
        },
        options: {
            plugins: {
                legend: {
                    display: false,
                },
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });
}

function cargarGraficoVentasMensuales(datosVentasMensuales) {
    const meses = Object.keys(datosVentasMensuales);
    const ventas = Object.values(datosVentasMensuales);

    const ctx4 = document.getElementById('chartVentasMensuales').getContext('2d');

    new Chart(ctx4, {
       type: 'line',
       data: {
           labels: meses,
           datasets: [{
               label: 'Ventas Mensuales',
               data: ventas,
               borderColor: 'rgb(75, 192, 192)',
               borderWidth: 2,
               fill: false
           }]
       },
       options: {
           plugins: {
               legend: {
                  display: false,
               },
           },
           scales: {
               yAxes: [{
                   ticks: {
                       beginAtZero: true
                   }
               }]
           }
       }
    });
}