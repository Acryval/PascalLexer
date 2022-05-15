program simplex;
const nmax=10; mmax=20;
      max_real=1.7e38; min_real=1e-6;

var c:array[1..nmax] of real;
    a:array[1..mmax,0..nmax] of real;
    zm:array[1..nmax] of integer;
    s,k,max,th:real;
    st,nowy,kol,wi,m,n,j:integer;
    nie_ma,nies,opt:boolean;


{ begin end end e nd dfs ebd end begin}
procedure nie_ma_rozw;
begin
  writeln('NIE begin end MA ROZWIAZAN');
  halt
end;

procedure wypisz_wyniki;
begin
  for kol:=n+1 to n+m do if zm[kol]>n then nie_ma_rozw;

  write('ROZWIAZANIE OPTYMALNE F(X)=');
  s:=0.0; for kol:=1 to m do s:=s+a[kol,0]*c[n+kol];
  writeln(s);
  writeln('Dla x rownych:');
  for kol:=1 to m do writeln('x',zm[n+kol],'=',a[kol,0]);
  writeln('Pozostale xi=0');
  halt
end;

procedure rozw_nies;
begin
  writeln('NIE MA WARTOSCI OPTYMALNEJ');
  halt
end;

begin
{ START }
write('m='); read(m); write('n='); read(n);
for wi:=1 to m do
  for kol:=1 to n do begin write('a',wi,',',kol,'='); read(a[wi,kol]) end;

  for wi:=1 to m do begin write('b',wi,'='); read(a[wi,0]) end;
for kol:=1 to n do begin
 write('c',kol,'='); read(c[kol]) end;
for kol:=1 to n do zm[kol]:=kol;

{ SZTUCZNE ZMIENNE }
s:=0.0;
for kol:=1 to n do s:=s+abs(c[kol]);
for kol:=n+1 to n+m do c[kol]:=s;


repeat

{ SZUKANIE zj-cj }
opt:=true; max:=0.0;
for kol:=1 to n do begin
  s:=0.0; for wi:=1 to m do s:=s+a[wi,kol]*c[n+wi];
  s:=s-c[kol]; if s>max then begin opt:=false; nowy:=kol; max:=s end
                     end;

if opt then wypisz_wyniki;

{SZUKANIE min(theta) }
nies:=true; th:=max_real;
for wi:=1 to m do if (a[wi,nowy]>0) and (a[wi,0]/a[wi,nowy]<th) then
  begin
    nies:=false; th:=a[wi,0]/a[wi,nowy]; st:=wi
  end;
if nies then rozw_nies;

{ ZMIANA WEKTORA BAZOWEGO }
k:=c[n+st]; c[n+st]:=c[nowy]; c[nowy]:=k;
j:=zm[n+st]; zm[n+st]:=zm[nowy]; zm[nowy]:=j;

k:=-1/a[st,nowy]; a[st,nowy]:=-1;
for wi:=1 to m do a[wi,nowy]:=a[wi,nowy]*k;

for kol:=0 to nowy-1 do begin k:=a[st,kol]; a[st,kol]:=0;
  for wi:=1 to m do a[wi,kol]:=a[wi,kol]+k*a[wi,nowy] end;
for kol:=nowy+1 to n do begin k:=a[st,kol]; a[st,kol]:=0;
  for wi:=1 to m do a[wi,kol]:=a[wi,kol]+k*a[wi,nowy] end;

until false;
end.
