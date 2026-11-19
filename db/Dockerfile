FROM almalinux:9
RUN dnf -y install https://download.postgresql.org/pub/repos/yum/reporpms/EL-9-x86_64/pgdg-redhat-repo-latest.noarch.rpm \
 && dnf -qy module disable postgresql \
 && dnf -y install postgresql16-server postgresql16-contrib util-linux \
 && dnf clean all
ENV PATH="/usr/pgsql-16/bin:${PATH}" PGDATA=/var/lib/pgsql/16/data
RUN mkdir -p /var/lib/pgsql/16 /docker-entrypoint-initdb.d && chown -R postgres:postgres /var/lib/pgsql/16
COPY init/ /docker-entrypoint-initdb.d/
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
EXPOSE 5432
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
