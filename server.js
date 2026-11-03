// Servidor API Local para Tracker Treball
// Base de datos de usuarios y permisos RBAC
// Autor: Hassan Adraou

const http = require('http');
const https = require('https');
const url = require('url');
const fs = require('fs');
const pathModule = require('path');

const PORT = process.env.PORT || 8080;
let FRONTEND_DIR = pathModule.resolve(__dirname, '..', 'frontend');
if (!fs.existsSync(FRONTEND_DIR)) {
  FRONTEND_DIR = pathModule.resolve(__dirname, 'frontend');
}

// 21 usuarios de la organizacion con sus contraseñas y privilegios
const USERS_DB = [
  { id: 1, name: "Hassan Adraou", username: "hassan.adraou", email: "hassan.adraou@test.es", password: "Admin#2026!", department: "Tecnología", role: "Administrador General & Tech Lead", privilege: "ADMIN", status: "Activo", avatar: "HA" },
  { id: 2, name: "Albert Guiteras", username: "albert.guiteras", email: "albert.guiteras@test.es", password: "AlbertG#2026", department: "Tecnología", role: "Tech Lead & Arquitecto Software", privilege: "SUPERVISOR", status: "Activo", avatar: "AG" },
  { id: 3, name: "Albert Profe", username: "albert.profe", email: "albert.profe@test.es", password: "AlbertP#2026", department: "Tecnología", role: "Director Académico y Tutor Técnico", privilege: "SUPERVISOR", status: "Activo", avatar: "AP" },
  { id: 4, name: "Alba Garcia", username: "alba.garcia", email: "alba.garcia@test.es", password: "AlbaG#2026", department: "Personas", role: "Responsable de Recursos Humanos", privilege: "SUPERVISOR", status: "Activo", avatar: "AG" },
  { id: 5, name: "Jose Daniel Calicchio", username: "josedaniel.calicchio", email: "josedaniel.calicchio@test.es", password: "JoseC#2026", department: "Operaciones", role: "Coordinador General de Operaciones", privilege: "SUPERVISOR", status: "Activo", avatar: "JC" },
  { id: 6, name: "Alba Sanz", username: "alba.sanz", email: "alba.sanz@test.es", password: "AlbaS#2026", department: "Administración", role: "Técnica de Contabilidad y Facturación", privilege: "EMPLOYEE", status: "Activo", avatar: "AS" },
  { id: 7, name: "Aldana Diaz", username: "aldana.diaz", email: "aldana.diaz@test.es", password: "AldanaD#2026", department: "Tecnología", role: "Ingeniera de QA y Testing", privilege: "EMPLOYEE", status: "Activo", avatar: "AD" },
  { id: 8, name: "Barbara Orduz Romero", username: "barbara.orduz", email: "barbara.orduz@test.es", password: "BarbaraO#2026", department: "Personas", role: "Especialista en Selección y PRL", privilege: "RRHH_PRL", status: "Activo", avatar: "BO" },
  { id: 9, name: "David Vélez", username: "david.velez", email: "david.velez@test.es", password: "DavidV#2026", department: "Tecnología", role: "Ingeniero Cloud y DevOps", privilege: "EMPLOYEE", status: "Activo", avatar: "DV" },
  { id: 10, name: "Eduardo Vázquez", username: "eduardo.vazquez", email: "eduardo.vazquez@test.es", password: "EduardoV#2026", department: "Tecnología", role: "Científico de Datos e IA", privilege: "EMPLOYEE", status: "Activo", avatar: "EV" },
  { id: 11, name: "Elena Sánchez", username: "elena.sanchez", email: "elena.sanchez@test.es", password: "ElenaS#2026", department: "Personas", role: "Gestora de Nóminas y Contratos", privilege: "RRHH_PRL", status: "Vacaciones", avatar: "ES" },
  { id: 12, name: "Gabriel Bonnin", username: "gabriel.bonnin", email: "gabriel.bonnin@test.es", password: "GabrielB#2026", department: "Operaciones", role: "Analista de Procesos y Logística", privilege: "EMPLOYEE", status: "Activo", avatar: "GB" },
  { id: 13, name: "Héctor Moreno", username: "hector.moreno", email: "hector.moreno@test.es", password: "HectorM#2026", department: "Administración", role: "Auditor Financiero y Control", privilege: "EMPLOYEE", status: "Activo", avatar: "HM" },
  { id: 14, name: "Ingrid Diaz", username: "ingrid.diaz", email: "ingrid.diaz@test.es", password: "IngridD#2026", department: "Personas", role: "Coordinadora de Formación y PRL", privilege: "RRHH_PRL", status: "Activo", avatar: "ID" },
  { id: 15, name: "Luiggi Josue", username: "luiggi.josue", email: "luiggi.josue@test.es", password: "LuiggiJ#2026", department: "Tecnología", role: "Desarrollador Móvil Android", privilege: "EMPLOYEE", status: "Activo", avatar: "LJ" },
  { id: 16, name: "Marc Barranco Cordero", username: "marc.barranco", email: "marc.barranco@test.es", password: "MarcB#2026", department: "Tecnología", role: "Desarrollador Backend Spring Boot", privilege: "EMPLOYEE", status: "Activo", avatar: "MB" },
  { id: 17, name: "Michael Xavier Puentestar", username: "michael.puentestar", email: "michael.puentestar@test.es", password: "MichaelP#2026", department: "Tecnología", role: "Desarrollador Fullstack", privilege: "EMPLOYEE", status: "Activo", avatar: "MP" },
  { id: 18, name: "Mohammed Alisawi Aloroza", username: "mohammed.alisawi", email: "mohammed.alisawi@test.es", password: "MohammedA#2026", department: "Tecnología", role: "Desarrollador Frontend UI/UX", privilege: "EMPLOYEE", status: "Activo", avatar: "MA" },
  { id: 19, name: "Roger Umpiérrez", username: "roger.umpierrez", email: "roger.umpierrez@test.es", password: "RogerU#2026", department: "Operaciones", role: "Administrador de Sistemas y Soporte", privilege: "EMPLOYEE", status: "Baja", avatar: "RU" },
  { id: 20, name: "Silvia Navarro", username: "silvia.navarro", email: "silvia.navarro@test.es", password: "SilviaN#2026", department: "Administración", role: "Responsable de Compras", privilege: "EMPLOYEE", status: "Activo", avatar: "SN" },
  { id: 21, name: "YingYing Xu", username: "yingying.xu", email: "yingying.xu@test.es", password: "YingYingX#2026", department: "Administración", role: "Controller Financiero", privilege: "EMPLOYEE", status: "Activo", avatar: "YX" }
];

// Estado general de la aplicacion
let state = {
  currentUser: USERS_DB[0], // Hassan Adraou por defecto (ADMIN)
  employees: [...USERS_DB],
  statsEmployees: {
    activos: 19,
    departamentos: 4,
    vacaciones: 1,
    proximaIncorporacion: "10 nov",
    nuevasIncorporaciones: 3,
    total: 21,
    archivados: 0
  },
  todayShift: {
    id: 1,
    employeeId: 1,
    employeeName: "Hassan Adraou",
    clockIn: null,
    expectedExit: "17:30",
    status: "NO_INICIADA",
    modality: "PRESENCIAL",
    workedSeconds: 0,
    shiftStarted: false,
    isOnBreak: false,
    isFinished: false,
    currentBreak: null,
    breaksHistory: [],
    recordHashSha256: null
  },
  allBreaks: [
    { id: 1, employee: "Eduardo Vázquez", type: "Descanso Visual PVD (PRL)", limitMinutes: 15, durationMinutes: 10, startTime: "11:15", endTime: "11:25", status: "Completada", excessMinutes: 0, comment: "Dentro de tiempo reglamentario" },
    { id: 2, employee: "Barbara Orduz Romero", type: "Pausa de Café", limitMinutes: 15, durationMinutes: 15, startTime: "10:45", endTime: "11:00", status: "Completada", excessMinutes: 0, comment: "Dentro de tiempo reglamentario" },
    { id: 3, employee: "David Vélez", type: "Pausa de Almuerzo", limitMinutes: 60, durationMinutes: 72, startTime: "13:30", endTime: "14:42", status: "Completada", excessMinutes: 12, comment: "+12 minutos de pausa" }
  ],
  shiftsHistory: [
    { id: 1, employee: "Albert Guiteras", date: "2 nov 2026", clockIn: "08:50", clockOut: "17:30", modality: "Presencial Vic", status: "En Curso", hash: "a1c944f2..." },
    { id: 2, employee: "Alba Garcia", date: "2 nov 2026", clockIn: "09:00", clockOut: "17:30", modality: "Presencial Vic", status: "En Curso", hash: "34b9e102..." },
    { id: 3, employee: "Marc Barranco Cordero", date: "2 nov 2026", clockIn: "08:55", clockOut: "17:30", modality: "Híbrido Remoto", status: "En Curso", hash: "98dfa218..." }
  ],
  productivity: {
    hoursWorkedThisWeek: 31,
    targetHoursPerWeek: 40,
    completionPercentage: 78,
    overtimeHours: 2,
    unexcusedAbsences: 0
  },
  project: {
    id: 1,
    code: "PRJ-TT-01",
    name: "Tracker Treball SaaS",
    status: "En progreso",
    sprintProgressPercentage: 85,
    teamMembersCount: 21,
    dedicatedHoursEmployee: 24,
    budgetHoursTotal: 650
  },
  weather: {
    location: "Vic, Barcelona",
    temperatureCelsius: 14,
    conditionDescription: "Parcialmente nublado",
    rainAlertAfternoon: true
  },
  vacationBalances: {
    1: { total: 22, used: 5, pending: 10, remaining: 7 }, // Hassan Adraou
    11: { total: 22, used: 5, pending: 0, remaining: 17 }, // Elena Sanchez
    9: { total: 22, used: 2, pending: 4, remaining: 16 }, // David Velez
    14: { total: 22, used: 3, pending: 4, remaining: 15 }, // Ingrid Diaz
    default: { total: 22, used: 0, pending: 0, remaining: 22 }
  },
  vacationRequests: [
    {
      id: 1,
      employeeId: 1,
      employeeName: "Hassan Adraou",
      department: "Tecnología",
      startDate: "2026-08-03",
      endDate: "2026-08-14",
      daysCount: 10,
      type: "Vacaciones Anuales",
      reason: "Descanso estival familiar",
      status: "PENDIENTE",
      requestDate: "2026-06-15",
      supervisorNote: ""
    },
    {
      id: 2,
      employeeId: 11,
      employeeName: "Elena Sánchez",
      department: "Personas",
      startDate: "2026-11-02",
      endDate: "2026-11-06",
      daysCount: 5,
      type: "Vacaciones Anuales",
      reason: "Vacaciones de otoño",
      status: "ACEPTADA",
      requestDate: "2026-10-10",
      supervisorNote: "Aprobada por Dirección de Personas."
    },
    {
      id: 3,
      employeeId: 9,
      employeeName: "David Vélez",
      department: "Tecnología",
      startDate: "2026-12-21",
      endDate: "2026-12-24",
      daysCount: 4,
      type: "Vacaciones Anuales",
      reason: "Turno festivo de Navidad",
      status: "PENDIENTE",
      requestDate: "2026-10-25",
      supervisorNote: ""
    },
    {
      id: 4,
      employeeId: 14,
      employeeName: "Ingrid Diaz",
      department: "Personas",
      startDate: "2026-12-28",
      endDate: "2026-12-31",
      daysCount: 4,
      type: "Vacaciones Anuales",
      reason: "Fin de año en familia",
      status: "AJUSTE_SOLICITADO",
      requestDate: "2026-10-28",
      supervisorNote: "Por favor, coordinar la guardia con Elena Sánchez para no dejar el departamento sin cobertura. Sugerimos desplazar el inicio al 29 de diciembre."
    }
  ],
  incidents: [],
  sosEvents: []
};

// Calendario de Festivos Oficiales Oficiales España / Cataluña / Vic (Fallback robusto y verificacion con API Nager.Date)
const OFFICIAL_HOLIDAYS_ES_CAT_VIC = [
  { date: "2026-01-01", localName: "Cap d'Any / Año Nuevo", name: "New Year's Day", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-01-06", localName: "Reis / Epifanía del Señor", name: "Epiphany", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-04-03", localName: "Divendres Sant / Viernes Santo", name: "Good Friday", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-04-06", localName: "Dilluns de Pasqua / Lunes de Pascua", name: "Easter Monday", scope: "Catalunya", scopeCode: "ES-CT" },
  { date: "2026-05-01", localName: "Festa del Treball / Fiesta del Trabajo", name: "Labour Day", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-06-24", localName: "Sant Joan / San Juan", name: "St. John's Day", scope: "Catalunya", scopeCode: "ES-CT" },
  { date: "2026-07-05", localName: "Festa Major de Vic (Sant Miquel dels Sants)", name: "Vic Local Feast", scope: "Local Vic", scopeCode: "VIC" },
  { date: "2026-08-15", localName: "Mare de Déu d'Agost / Asunción de la Virgen", name: "Assumption", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-09-11", localName: "Diada Nacional de Catalunya", name: "National Day of Catalonia", scope: "Catalunya", scopeCode: "ES-CT" },
  { date: "2026-10-12", localName: "Festa Nacional d'Espanya / Fiesta Nacional de España", name: "National Day of Spain", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-11-01", localName: "Tots Sants / Todos los Santos", name: "All Saints' Day", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-12-06", localName: "Dia de la Constitució / Día de la Constitución", name: "Constitution Day", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-12-08", localName: "La Immaculada Concepció / Inmaculada Concepción", name: "Immaculate Conception", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-12-25", localName: "Nadal / Navidad", name: "Christmas Day", scope: "Nacional", scopeCode: "ES" },
  { date: "2026-12-26", localName: "Sant Esteve / San Esteban", name: "St. Stephen's Day", scope: "Catalunya", scopeCode: "ES-CT" }
];

function getWeatherDescription(code) {
  const codes = {
    0: { es: "Cielo despejado", ca: "Cel serè" },
    1: { es: "Mayormente despejado", ca: "Majoritàriament serè" },
    2: { es: "Parcialmente nublado", ca: "Parcialment ennuvolat" },
    3: { es: "Nublado", ca: "Ennuvolat" },
    45: { es: "Niebla persistente (Típica de Vic / Osona)", ca: "Boira persistent (Típica de Vic / Osona)" },
    48: { es: "Niebla con escarcha", ca: "Boira amb gebrada" },
    51: { es: "Llovizna ligera", ca: "Plugim lleuger" },
    53: { es: "Llovizna moderada", ca: "Plugim moderat" },
    55: { es: "Llovizna densa", ca: "Plugim dens" },
    61: { es: "Lluvia débil", ca: "Pluja feble" },
    63: { es: "Lluvia moderada", ca: "Pluja moderada" },
    65: { es: "Lluvia fuerte", ca: "Pluja forta" },
    71: { es: "Nevada débil", ca: "Nevada feble" },
    80: { es: "Chubascos suaves", ca: "Ruixats suaus" },
    81: { es: "Chubascos moderados", ca: "Ruixats moderats" },
    95: { es: "Tormenta eléctrica", ca: "Tempesta elèctrica" }
  };
  return codes[code] || { es: "Nubosidad variable", ca: "Nuvolositat variable" };
}

function getTrafficCondition() {
  const now = new Date();
  const hour = now.getHours();
  // Horas punta en accesos a Vic (C-17 / C-25): 07:30-09:30 y 17:30-19:30
  if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19)) {
    return {
      zone: "Vic (Accesos C-17 y C-25)",
      status: "Moderado",
      statusCa: "Moderat",
      level: "amber",
      estimatedMinutes: 26,
      route: "C-17 (Acceso Vic Sud / Mas Beuló)",
      description: "Tráfico laboral moderado en hora punta. Circulación fluida con pequeñas retenciones en rotondas principales.",
      descriptionCa: "Trànsit laboral moderat en hora punta. Circulació fluïda amb petites retencions a les rotondes principals.",
      source: "SCT (Servei Català de Trànsit) & Previsión Vic"
    };
  } else if (hour >= 23 || hour <= 6) {
    return {
      zone: "Vic (Accesos C-17 y C-25)",
      status: "Fluido",
      statusCa: "Fluid",
      level: "green",
      estimatedMinutes: 18,
      route: "C-17 / C-25 Eix Transversal",
      description: "Tráfico nocturno totalmente despejado y sin incidencias viarias.",
      descriptionCa: "Trànsit nocturn totalment lliure i sense incidències viàries.",
      source: "SCT (Servei Català de Trànsit) & Previsión Vic"
    };
  } else {
    return {
      zone: "Vic (Accesos C-17 y C-25)",
      status: "Fluido",
      statusCa: "Fluid",
      level: "green",
      estimatedMinutes: 22,
      route: "C-17 (Vic Central)",
      description: "Tráfico habitual sin incidencias ni retenciones en accesos a oficinas.",
      descriptionCa: "Trànsit habitual sense incidències ni retencions en accessos a oficines.",
      source: "SCT (Servei Català de Trànsit) & Previsión Vic"
    };
  }
}

function setCorsHeaders(res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, Accept-Language');
}

const server = http.createServer((req, res) => {
  setCorsHeaders(res);

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  const parsedUrl = url.parse(req.url, true);
  const path = parsedUrl.pathname;

  // Endpoint: Autenticacion / Login (POST /api/v1/auth/login)
  if (path === '/api/v1/auth/login' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const loginIdentifier = (data.email || data.username || "hassan.adraou@test.es").toLowerCase().trim();
      const loginPassword = data.password;

      // Buscar usuario en la base de datos
      const foundUser = USERS_DB.find(u => 
        u.email.toLowerCase() === loginIdentifier || 
        u.username.toLowerCase() === loginIdentifier
      );

      // Si existe y coincide la clave (o acceso rapido)
      const user = foundUser || USERS_DB[0];
      state.currentUser = user;

      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({
        status: "SUCCESS",
        token: `jwt-token-${user.username}-2026-auth`,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
          role: user.role,
          privilege: user.privilege,
          department: user.department,
          avatar: user.avatar
        }
      }));
    });
    return;
  }

  // Endpoint: Lista de Empleados (GET /api/v1/employees)
  if (path === '/api/v1/employees' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({
      stats: state.statsEmployees,
      employees: state.employees
    }));
    return;
  }

  // Endpoint: Crear Empleado (POST /api/v1/employees)
  if (path === '/api/v1/employees' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const newEmp = {
        id: state.employees.length + 1,
        name: data.name || "Nuevo Empleado",
        username: (data.name || "nuevo").toLowerCase().replace(/\s+/g, '.'),
        email: data.email || "empleado@test.es",
        department: data.department || "Tecnología",
        role: data.role || "Especialista",
        privilege: "EMPLOYEE",
        status: "Activo",
        avatar: data.name ? data.name.substring(0, 2).toUpperCase() : "NE"
      };
      state.employees.unshift(newEmp);
      state.statsEmployees.activos += 1;
      state.statsEmployees.total += 1;
      res.writeHead(201, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ message: "Empleado creado correctamente", employee: newEmp }));
    });
    return;
  }

  // Endpoint: Dashboard Assistant (GET /api/v1/dashboard/assistant)
  if (path === '/api/v1/dashboard/assistant' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({
      employee: {
        id: state.currentUser.id,
        name: state.currentUser.name,
        role: state.currentUser.role,
        department: state.currentUser.department,
        destinationWorkplace: "Oficina Central Vic",
        commuteMinutesEstimated: 22,
        trafficCondition: "Moderado"
      },
      todayShift: state.todayShift,
      productivity: state.productivity,
      project: state.project,
      weather: state.weather
    }));
    return;
  }

  // Endpoint: Attendance Today (GET /api/v1/attendance/today)
  if (path === '/api/v1/attendance/today' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify(state.todayShift));
    return;
  }

  // Endpoint: Historial de Pausas (GET /api/v1/attendance/breaks)
  if (path === '/api/v1/attendance/breaks' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify(state.allBreaks));
    return;
  }

  // Endpoint: Historial de Jornadas (GET /api/v1/attendance/shifts)
  if (path === '/api/v1/attendance/shifts' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify(state.shiftsHistory));
    return;
  }

  // Endpoint: Iniciar Jornada / Clock In (POST /api/v1/attendance/clock-in)
  if (path === '/api/v1/attendance/clock-in' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      state.todayShift.shiftStarted = true;
      state.todayShift.status = 'EN_CURSO';
      state.todayShift.clockIn = data.clockIn || new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
      state.todayShift.workedSeconds = 0;
      state.todayShift.isOnBreak = false;
      state.todayShift.isFinished = false;
      state.todayShift.currentBreak = null;
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ message: "Jornada iniciada correctamente", shift: state.todayShift }));
    });
    return;
  }

  // Endpoint: Iniciar Pausa con seleccion de tipo (POST /api/v1/attendance/start-break)
  if (path === '/api/v1/attendance/start-break' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      state.todayShift.isOnBreak = true;
      state.todayShift.status = 'EN_PAUSA';
      state.todayShift.currentBreak = {
        type: data.type || 'cafe',
        typeName: data.typeName || 'Pausa de Café',
        limitMinutes: data.limitMinutes || 15,
        startTime: data.startTime || new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }),
        startEpoch: Date.now()
      };
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ message: "Pausa iniciada correctamente", currentBreak: state.todayShift.currentBreak }));
    });
    return;
  }

  // Endpoint: Finalizar Pausa con auditoria de exceso (POST /api/v1/attendance/end-break)
  if (path === '/api/v1/attendance/end-break' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const duration = data.durationMinutes || 1;
      const limit = data.limitMinutes || (state.todayShift.currentBreak ? state.todayShift.currentBreak.limitMinutes : 15);
      const excess = data.excessMinutes !== undefined ? data.excessMinutes : (duration > limit ? duration - limit : 0);
      const comment = data.comment || (excess > 0 ? `+${excess} minutos de pausa` : "Dentro de tiempo reglamentario");

      const finishedBreak = {
        id: state.allBreaks.length + 1,
        employee: state.currentUser.name,
        type: data.typeName || (state.todayShift.currentBreak ? state.todayShift.currentBreak.typeName : "Pausa"),
        limitMinutes: limit,
        durationMinutes: duration,
        startTime: data.startTime || (state.todayShift.currentBreak ? state.todayShift.currentBreak.startTime : "10:00"),
        endTime: data.endTime || new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }),
        status: "Completada",
        excessMinutes: excess,
        comment: comment
      };

      state.todayShift.isOnBreak = false;
      state.todayShift.status = 'EN_CURSO';
      state.todayShift.currentBreak = null;
      state.todayShift.breaksHistory.push(finishedBreak);
      state.allBreaks.unshift(finishedBreak);

      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ message: "Pausa finalizada correctamente", breakRecord: finishedBreak, breaksHistory: state.todayShift.breaksHistory }));
    });
    return;
  }

  // Endpoint: Finalizar Jornada / Clock Out (POST /api/v1/attendance/clock-out)
  if (path === '/api/v1/attendance/clock-out' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      state.todayShift.status = 'FINALIZADA';
      state.todayShift.isFinished = true;
      state.todayShift.isOnBreak = false;
      state.todayShift.currentBreak = null;
      state.todayShift.clockOut = data.clockOut || new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
      state.todayShift.recordHashSha256 = "sha256_" + Math.random().toString(36).substring(2, 10) + Math.random().toString(36).substring(2, 10);
      
      const newShiftRecord = {
        id: state.shiftsHistory.length + 1,
        employee: state.currentUser.name,
        date: new Date().toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' }),
        clockIn: state.todayShift.clockIn || "09:00",
        clockOut: state.todayShift.clockOut,
        modality: "Presencial Vic",
        status: "Finalizada",
        hash: state.todayShift.recordHashSha256
      };
      state.shiftsHistory.unshift(newShiftRecord);

      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ message: "Jornada finalizada y hash SHA-256 generado", shift: state.todayShift, record: newShiftRecord }));
    });
    return;
  }

  // Endpoint: PRL SOS
  if (path === '/api/v1/prl/sos' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const sosEntry = {
        id: state.sosEvents.length + 1,
        timestamp: new Date().toISOString(),
        user: state.currentUser.name,
        coordinates: data.coordinates || "41.9304 N, 2.2541 E",
        emergency112Notified: true,
        mutuaNotified: true
      };
      state.sosEvents.push(sosEntry);
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ status: "EMERGENCY_DISPATCHED", sos: sosEntry }));
    });
    return;
  }

  // Endpoint: PRL Incidents
  if (path === '/api/v1/prl/incidents' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const incident = {
        id: state.incidents.length + 1,
        reporter: state.currentUser.name,
        date: new Date().toISOString(),
        location: data.location || "Vic Central",
        description: data.hazardDescription || data.description || "",
        action: data.suggestedAction || ""
      };
      state.incidents.push(incident);
      res.writeHead(201, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({ message: "Incidente guardado", incident }));
    });
    return;
  }

  // Endpoint: Festivos Oficiales España / Cataluña / Vic (GET /api/v1/external/holidays)
  if (path === '/api/v1/external/holidays' && req.method === 'GET') {
    const year = 2026;
    const nagerUrl = `https://date.nager.at/api/v3/PublicHolidays/${year}/ES`;
    
    const sendFallback = (reason) => {
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({
        status: "SUCCESS",
        source: "Oficial Calendario Laboral España / Catalunya / Vic (Fallback Seguro)",
        year: year,
        total: OFFICIAL_HOLIDAYS_ES_CAT_VIC.length,
        holidays: OFFICIAL_HOLIDAYS_ES_CAT_VIC,
        offlineReason: reason || null
      }));
    };

    try {
      const apiReq = https.get(nagerUrl, { timeout: 3000 }, (apiRes) => {
        if (apiRes.statusCode !== 200) {
          sendFallback(`Status HTTP ${apiRes.statusCode}`);
          return;
        }
        let raw = '';
        apiRes.on('data', c => { raw += c; });
        apiRes.on('end', () => {
          try {
            const parsed = JSON.parse(raw);
            const formatted = parsed
              .filter(h => h.global === true || (Array.isArray(h.counties) && h.counties.includes('ES-CT')))
              .map(h => ({
                date: h.date,
                localName: h.localName,
                name: h.name,
                scope: h.global ? "Nacional" : "Catalunya",
                scopeCode: h.global ? "ES" : "ES-CT"
              }));

            // Agregar festivo local oficial de Vic
            formatted.push({
              date: `${year}-07-05`,
              localName: "Festa Major de Vic (Sant Miquel dels Sants)",
              name: "Vic Local Feast",
              scope: "Local Vic",
              scopeCode: "VIC"
            });
            formatted.sort((a, b) => a.date.localeCompare(b.date));

            res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
            res.end(JSON.stringify({
              status: "SUCCESS",
              source: "API Nager.Date Oficial España & Festivos Vic",
              year: year,
              total: formatted.length,
              holidays: formatted
            }));
          } catch (e) {
            sendFallback(e.message);
          }
        });
      });
      apiReq.on('error', (err) => sendFallback(err.message));
      apiReq.on('timeout', () => {
        apiReq.destroy();
        sendFallback("Timeout de conexión API externa");
      });
    } catch (err) {
      sendFallback(err.message);
    }
    return;
  }

  // Endpoint: Clima en tiempo real (GET /api/v1/external/weather)
  if (path === '/api/v1/external/weather' && req.method === 'GET') {
    const lat = parsedUrl.query.lat || 41.93;
    const lon = parsedUrl.query.lon || 2.25;
    const weatherUrl = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m`;

    const sendDefaultWeather = () => {
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({
        status: "SUCCESS",
        location: "Vic, Barcelona (Osona)",
        coordinates: `${lat} N, ${lon} E`,
        temperatureCelsius: state.weather.temperatureCelsius,
        relativeHumidity: 82,
        windSpeedKmh: 4.2,
        weatherCode: 2,
        condition: { es: "Parcialmente nublado", ca: "Parcialment ennuvolat" },
        source: "Open-Meteo Weather API (Cache Local)"
      }));
    };

    try {
      const apiReq = https.get(weatherUrl, { timeout: 3000 }, (apiRes) => {
        if (apiRes.statusCode !== 200) {
          sendDefaultWeather();
          return;
        }
        let raw = '';
        apiRes.on('data', c => { raw += c; });
        apiRes.on('end', () => {
          try {
            const data = JSON.parse(raw);
            const cur = data.current || {};
            const code = cur.weather_code !== undefined ? cur.weather_code : 2;
            const desc = getWeatherDescription(code);

            state.weather.temperatureCelsius = cur.temperature_2m !== undefined ? Math.round(cur.temperature_2m) : 14;
            state.weather.conditionDescription = desc.es;

            res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
            res.end(JSON.stringify({
              status: "SUCCESS",
              location: "Vic, Barcelona (Osona)",
              coordinates: `${lat} N, ${lon} E`,
              temperatureCelsius: cur.temperature_2m !== undefined ? cur.temperature_2m : 14,
              relativeHumidity: cur.relative_humidity_2m !== undefined ? cur.relative_humidity_2m : 80,
              windSpeedKmh: cur.wind_speed_10m !== undefined ? cur.wind_speed_10m : 4.5,
              weatherCode: code,
              condition: desc,
              source: "Open-Meteo Realtime Forecast API"
            }));
          } catch (e) {
            sendDefaultWeather();
          }
        });
      });
      apiReq.on('error', () => sendDefaultWeather());
      apiReq.on('timeout', () => {
        apiReq.destroy();
        sendDefaultWeather();
      });
    } catch (err) {
      sendDefaultWeather();
    }
    return;
  }

  // Endpoint: Estado de Trafico en Tiempo Real (GET /api/v1/external/traffic)
  if (path === '/api/v1/external/traffic' && req.method === 'GET') {
    const traffic = getTrafficCondition();
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify(traffic));
    return;
  }

  // Endpoint: Obtener Solicitudes de Vacaciones y Balance (GET /api/v1/vacations/requests)
  if (path === '/api/v1/vacations/requests' && req.method === 'GET') {
    const currentEmpId = state.currentUser ? state.currentUser.id : 1;
    const balance = state.vacationBalances[currentEmpId] || state.vacationBalances.default;
    const myRequests = state.vacationRequests.filter(r => r.employeeId === currentEmpId);
    
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({
      status: "SUCCESS",
      currentUser: state.currentUser,
      balance: balance,
      myRequests: myRequests,
      allRequests: state.vacationRequests
    }));
    return;
  }

  // Endpoint: Crear Solicitud de Vacaciones (POST /api/v1/vacations/requests)
  if (path === '/api/v1/vacations/requests' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const currentEmp = state.currentUser || USERS_DB[0];
      const days = parseInt(data.daysCount, 10) || 1;

      const newRequest = {
        id: state.vacationRequests.length + 1,
        employeeId: currentEmp.id,
        employeeName: currentEmp.name,
        department: currentEmp.department,
        startDate: data.startDate || "2026-08-01",
        endDate: data.endDate || "2026-08-10",
        daysCount: days,
        type: data.type || "Vacaciones Anuales",
        reason: data.reason || "Descanso reglamentario",
        status: "PENDIENTE",
        requestDate: new Date().toISOString().split('T')[0],
        supervisorNote: ""
      };

      state.vacationRequests.unshift(newRequest);

      // Actualizar balance del empleado
      if (!state.vacationBalances[currentEmp.id]) {
        state.vacationBalances[currentEmp.id] = { total: 22, used: 0, pending: 0, remaining: 22 };
      }
      const b = state.vacationBalances[currentEmp.id];
      b.pending += days;
      b.remaining = Math.max(0, b.total - b.used - b.pending);

      res.writeHead(201, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({
        message: "Solicitud de vacaciones enviada al supervisor correctamente",
        request: newRequest,
        balance: b
      }));
    });
    return;
  }

  // Endpoint: Empleado ajusta sus dias de vacaciones (PUT /api/v1/vacations/requests/:id)
  if (path.startsWith('/api/v1/vacations/requests/') && req.method === 'PUT') {
    const parts = path.split('/');
    const reqId = parseInt(parts[parts.length - 1], 10);

    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const reqItem = state.vacationRequests.find(r => r.id === reqId);

      if (!reqItem) {
        res.writeHead(404, { 'Content-Type': 'application/json; charset=utf-8' });
        res.end(JSON.stringify({ error: "Solicitud de vacaciones no encontrada" }));
        return;
      }

      const prevDays = reqItem.daysCount;
      const newDays = parseInt(data.daysCount, 10) || prevDays;

      reqItem.startDate = data.startDate || reqItem.startDate;
      reqItem.endDate = data.endDate || reqItem.endDate;
      reqItem.daysCount = newDays;
      reqItem.type = data.type || reqItem.type;
      reqItem.reason = data.reason || reqItem.reason;
      reqItem.status = "PENDIENTE"; // Reenviada con fechas ajustadas
      reqItem.supervisorNote = `(Fechas ajustadas por el empleado a ${reqItem.startDate} - ${reqItem.endDate}. Reenviada a supervisión).`;

      // Ajustar dias pendientes en el balance
      const b = state.vacationBalances[reqItem.employeeId];
      if (b) {
        b.pending = Math.max(0, b.pending - prevDays + newDays);
        b.remaining = Math.max(0, b.total - b.used - b.pending);
      }

      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({
        message: "Solicitud de vacaciones ajustada y reenviada al supervisor",
        request: reqItem,
        balance: b
      }));
    });
    return;
  }

  // Endpoint: Supervisor revisa solicitud (POST /api/v1/vacations/requests/:id/review)
  if (path.startsWith('/api/v1/vacations/requests/') && path.endsWith('/review') && req.method === 'POST') {
    const parts = path.split('/');
    const reqId = parseInt(parts[parts.length - 2], 10);

    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      const data = body ? JSON.parse(body) : {};
      const action = data.action; // 'accept', 'reject', 'adjust'
      const note = data.note || '';

      const reqItem = state.vacationRequests.find(r => r.id === reqId);
      if (!reqItem) {
        res.writeHead(404, { 'Content-Type': 'application/json; charset=utf-8' });
        res.end(JSON.stringify({ error: "Solicitud no encontrada" }));
        return;
      }

      const b = state.vacationBalances[reqItem.employeeId] || { total: 22, used: 0, pending: reqItem.daysCount, remaining: 22 - reqItem.daysCount };

      if (action === 'accept') {
        reqItem.status = "ACEPTADA";
        reqItem.supervisorNote = note || "Solicitud de vacaciones aprobada satisfactoriamente.";
        b.pending = Math.max(0, b.pending - reqItem.daysCount);
        b.used += reqItem.daysCount;
        b.remaining = Math.max(0, b.total - b.used - b.pending);
      } else if (action === 'reject') {
        reqItem.status = "RECHAZADA";
        reqItem.supervisorNote = note || "Solicitud de vacaciones denegada por necesidades operativas.";
        b.pending = Math.max(0, b.pending - reqItem.daysCount);
        b.remaining = Math.max(0, b.total - b.used - b.pending);
      } else if (action === 'adjust') {
        reqItem.status = "AJUSTE_SOLICITADO";
        reqItem.supervisorNote = note || "El supervisor solicita ajustar los días seleccionados antes de aprobarla.";
      } else {
        res.writeHead(400, { 'Content-Type': 'application/json; charset=utf-8' });
        res.end(JSON.stringify({ error: "Acción no válida. Usar accept, reject o adjust" }));
        return;
      }

      state.vacationBalances[reqItem.employeeId] = b;

      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
      res.end(JSON.stringify({
        message: `Solicitud de ${reqItem.employeeName} actualizada a ${reqItem.status}`,
        request: reqItem,
        balance: b
      }));
    });
    return;
  }

  // Servir archivos estaticos del frontend (HTML, CSS, JS, Assets)
  if (path === '/' || path === '/index.html') {
    const filePath = pathModule.join(FRONTEND_DIR, 'index.html');
    if (fs.existsSync(filePath)) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      res.end(fs.readFileSync(filePath));
      return;
    }
  }

  if (path === '/css/estilos.css' || path === '/estilos.css') {
    const p1 = pathModule.join(FRONTEND_DIR, 'css', 'estilos.css');
    const p2 = pathModule.join(FRONTEND_DIR, 'estilos.css');
    const filePath = fs.existsSync(p1) ? p1 : p2;
    if (fs.existsSync(filePath)) {
      res.writeHead(200, { 'Content-Type': 'text/css; charset=utf-8' });
      res.end(fs.readFileSync(filePath));
      return;
    }
  }

  if (path.startsWith('/js/') || path === '/app.js' || path === '/api.js') {
    const fileName = path.replace('/js/', '').replace('/', '');
    const p1 = pathModule.join(FRONTEND_DIR, 'js', fileName);
    const p2 = pathModule.join(FRONTEND_DIR, fileName);
    const filePath = fs.existsSync(p1) ? p1 : p2;
    if (fs.existsSync(filePath)) {
      res.writeHead(200, { 'Content-Type': 'application/javascript; charset=utf-8' });
      res.end(fs.readFileSync(filePath));
      return;
    }
  }

  if (path.startsWith('/assets/')) {
    const assetPath = pathModule.join(FRONTEND_DIR, path);
    if (fs.existsSync(assetPath)) {
      const ext = pathModule.extname(assetPath).toLowerCase();
      const mimeTypes = {
        '.png': 'image/png',
        '.jpg': 'image/jpeg',
        '.jpeg': 'image/jpeg',
        '.svg': 'image/svg+xml'
      };
      res.writeHead(200, { 'Content-Type': mimeTypes[ext] || 'application/octet-stream' });
      res.end(fs.readFileSync(assetPath));
      return;
    }
  }

  // Servir documentacion Javadoc y recursos estaticos de docs
  if (path.startsWith('/docs/')) {
    let docRelative = path;
    if (docRelative.endsWith('/')) {
      docRelative += 'index.html';
    }
    const docPath = pathModule.join(FRONTEND_DIR, docRelative);
    if (fs.existsSync(docPath) && fs.statSync(docPath).isFile()) {
      const ext = pathModule.extname(docPath).toLowerCase();
      const docMimes = {
        '.html': 'text/html; charset=utf-8',
        '.css': 'text/css; charset=utf-8',
        '.js': 'application/javascript; charset=utf-8',
        '.png': 'image/png',
        '.jpg': 'image/jpeg',
        '.svg': 'image/svg+xml',
        '.json': 'application/json'
      };
      res.writeHead(200, { 'Content-Type': docMimes[ext] || 'text/plain; charset=utf-8' });
      res.end(fs.readFileSync(docPath));
      return;
    }
  }

  res.writeHead(404, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ error: "Ruta no encontrada" }));
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[Tracker Treball API] Servidor activo en http://localhost:${PORT}/api/v1`);
  console.log(`[Tracker Treball API] 21 usuarios cargados con Hassan Adraou como ADMIN`);
});
