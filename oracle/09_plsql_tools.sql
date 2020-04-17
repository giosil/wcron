--
-- Tools
--

declare
cursor c0 is
select name, ins_date
from activities
where uri = 'mock'
order by ins_date;

vcActName varchar2(50);
tsInsDate timestamp;
countJobs number;

begin
  
  open c0;

  loop
  fetch c0 into vcActName, tsInsDate;
    exit when c0%NOTFOUND;
    
    begin
      select count(*)
      into countJobs
      from jobs
      where activity = vcActName;
    exception
    when no_data_found then
      countJobs := 0;
    end;
    
    if(countJobs != 0) then
      delete from jobs where activity = vcActName;
    end if;
    
  end loop;

  close c0;
end;
/

commit;